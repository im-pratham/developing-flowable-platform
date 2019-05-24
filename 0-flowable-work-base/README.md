# Flowable Work Skeleton Project

This is the Flowable Work Skeleton project for use in Flowable Training.
It is a Spring Boot Application pulling some Flowable dependencies to create a fully featured
Flowable Work Application that can be built upon. 

## Additional configuration

### Security
As security is always project specific, this project also contains some "default"
configuration classes to set up basic authentication against the Flowable DB.

### Tenant-Setup`
A minimal tenant setup is provided with an admin user (`admin`) and a non-admin user (`user`). It is important
for the admin user to have the `flowableAdministrator` group, as this signifies a 'super-admin' to the platform.

The default passwords for these users is defined through the `flowable.platform.idm.default-password` property in `application.properties