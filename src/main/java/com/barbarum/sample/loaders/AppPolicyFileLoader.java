package com.barbarum.sample.loaders;

import com.barbarum.sample.loaders.models.Policies;
import com.barbarum.sample.loaders.models.PolicyEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.Resource;


@Slf4j
public class AppPolicyFileLoader {

    private static final char COMMENT_PLACE_HOLDER = '#'; 

    private static final char FIELD_DELIMITER = ',';

    private static final String ARBITRARY_FIELD_VALUE_PLACE_HOLDER = "_";

    private final Resource resource;
    
    /**
     * @param resource
     */
    public AppPolicyFileLoader(Resource resource) {
        this.resource = resource;
    }

    public Policies load() throws IOException {
        if (this.resource == null) {
            log.warn("Application policy config is not specified, ignore policies loading.");
            return new Policies();
        }
        BufferedReader reader = IOUtils.toBufferedReader(new InputStreamReader(this.resource.getInputStream()));
        List<CSVRecord> records = this.getCsvFormat().parse(reader).getRecords();
        Policies policies = new Policies();
        records.stream()
            .filter(Objects::nonNull)
            .filter(e -> e.isSet(0)) // check if a line has content
            .map(this::toPolicyPair)
            .sorted(this::sortPolicies)
            .forEach(e -> this.handle(e, policies));
        return policies;
    }

    /**
     * Sort policies by the following rules: 
     *  1. Policies without entry type are sorted in the front.
     *  2. Then, policies with ROLE type.
     *  3. At last, policies sorted by entry type naturely.
     *  4. Besides, policies with the same entry type, sorted by FIFO pattern.
     */
    private int sortPolicies(Pair<PolicyEntry.EntryType, CSVRecord> a, Pair<PolicyEntry.EntryType, CSVRecord> b) {
        if (a.getKey() == null || b.getKey() == null) {
            return b != null ? 1 : 0;
        }
        if (a.getKey() == b.getKey()) {
            return 0;
        }
        if (a.getKey() == PolicyEntry.EntryType.ROLE) {
            return -1;
        }
        return a.getKey().compareTo(b.getKey());
    }

    private Pair<PolicyEntry.EntryType, CSVRecord> toPolicyPair(CSVRecord policyRecord) {
        PolicyEntry.EntryType entryType = this.getPolicyEntryType(policyRecord);
        return Pair.of(entryType, policyRecord);
    }

    private void handle(Pair<PolicyEntry.EntryType, CSVRecord> policyRecord, Policies policies) {
        PolicyEntry.EntryType entryType = policyRecord.getKey();
        if (entryType == null) {
            log.warn("Malformed csv record: {}", policyRecord);
            return;
        }
        switch (entryType) {
            case ROLE:
                this.handleRole(policyRecord.getValue(), policies);
                break;
            case POLICY:
                this.handlePolicy(policyRecord.getValue(), policies); 
                break;
            default:
                throw new UnsupportedOperationException("Unhandled policy entry: " + policyRecord.getValue());
        }
    }

    private void handleRole(CSVRecord policyRecord, Policies policies) {
        Optional<String> role = this.getParsedRecordField(policyRecord, 1);
        Optional<String> parent = this.getParsedRecordField(policyRecord, 2); 
        policies.addRole(role.orElse(null), parent.orElse(null));
    }

    private void handlePolicy(CSVRecord policyRecord, Policies policies) {
        Optional<String> role = this.getParsedRecordField(policyRecord, 1); 
        Optional<String> resourceValue = this.getParsedRecordField(policyRecord, 2); 
        Optional<String> operation = this.getParsedRecordField(policyRecord, 3); 
        Optional<String> effect = this.getParsedRecordField(policyRecord, 4);
        
        policies.addPolicy(
            role.orElse(null), 
            resourceValue.orElse(null), 
            operation.orElse(null), 
            effect.orElse(null)
        );

    }

    private CSVFormat getCsvFormat() {
        return CSVFormat.DEFAULT
            .withCommentMarker(COMMENT_PLACE_HOLDER)
            .withDelimiter(FIELD_DELIMITER);
    }

    private PolicyEntry.EntryType getPolicyEntryType(CSVRecord policyRecord) {
        String policyType = StringUtils.toRootUpperCase(StringUtils.trimToEmpty(policyRecord.get(0)));
        return PolicyEntry.EntryType.valueOf(policyType);
    }

    private Optional<String> getParsedRecordField(CSVRecord policyRecord, int position) {
        if (position < 0 || position >= policyRecord.size()) {
            return Optional.empty();
        }
        String result = StringUtils.trimToEmpty(policyRecord.get(position));
        return StringUtils.equals(result, ARBITRARY_FIELD_VALUE_PLACE_HOLDER) ? Optional.empty() : Optional.ofNullable(result);
    }
}
