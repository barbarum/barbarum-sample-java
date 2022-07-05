# barbarum-sample-java

Java application sample

## Features

### Authentication

```bash
./gradlew build bootRun -x test
```

Open [http://localhost:8080/me](http://localhost:8080/me)

### Authorization

#### RBAC

##### Has admin role

1. [Login](http://localhost:8080/login) with admin user `admin/demo`
2. Open [http://localhost:8080/users](http://localhost:8080/users)

##### Doesn't has admin role

1. Logout [http://localhost:8080/logout](http://localhost:8080/logout)
2. [Login](http://localhost:8080/login) with user `user/demmo`
3. Open [http://localhost:8080/users](http://localhost:8080/users)

#### ACL

**_WIP_**

## Notes

### Generate keystore.jks

```bash
keytool -genkey -alias jwtsigning -keyalg RSA -keystore keystore.jks -keysize 2048
```
