package com.barbarum.sample;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
        long actual = 0L;
        long expected = 0L;
        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
