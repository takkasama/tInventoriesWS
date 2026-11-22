# Java Maven Web Service Template

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta_EE-10-355C7D?style=flat-square)
![Swagger](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)
![WAR](https://img.shields.io/badge/Packaging-WAR-555555?style=flat-square)

A reusable **Java web service template** built with **Jakarta EE** and **Apache Maven**. It provides a starting point for REST APIs, OpenAPI documentation, and server-side applications packaged as WAR files.

> **Template status:** This repository provides a foundation, not a production-ready service. Review dependencies, server configuration, authentication, and application-specific settings before deployment.

## Technology stack

| Technology | Purpose |
| --- | --- |
| Java 17 | Java language level configured in Maven |
| Apache Maven | Dependency management and build lifecycle |
| Jakarta EE 10 | Jakarta enterprise APIs |
| Jakarta REST / Jersey | REST API development |
| Swagger Core / Swagger UI | OpenAPI generation and interactive documentation |
| JJWT | JWT support |
| PrimeFaces 16 | Jakarta Faces UI components |
| JasperReports 7 | Report generation (PDF, fonts and charts) |
| Apache POI | Office document processing |
| Angus Mail | Email support |
| Log4j | Logging |

## Requirements

- JDK 17 or a compatible newer JDK
- Apache Maven
- A Jakarta EE-compatible application server configured for the project's runtime dependencies (for example, a suitably configured Payara Server)

## Getting started

### Create a project from the template

Use [**Use this template**](https://github.com/takkasama/template-web-service-java-maven/generate), or clone it:

```bash
git clone https://github.com/takkasama/template-web-service-java-maven.git
cd template-web-service-java-maven
```

### Configure the Maven project

Edit `pom.xml` to change its Maven coordinates:

```xml
<groupId>com.example</groupId>
<artifactId>my-web-service</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>war</packaging>
```

Rename Java packages, REST application paths, and the OpenAPI resource package configuration to match your new project. Update any application URLs that include the former context path.

### Build

```bash
mvn clean package
```

The WAR will be generated in `target/`, provided the source code and dependencies compile successfully.

### Deploy

Deploy the generated WAR to your compatible application server (for example, using the server administration console or your IDE).

The effective context root depends on the deployment configuration and WAR name. If deployed under `template-web-service-java`, its base URL would typically be:

```text
http://localhost:8080/template-web-service-java/
```

## API documentation

The Maven build is configured to retrieve **Swagger UI** from WebJars and copy it into the deployed application's `docs/` directory.

If the application is deployed with the context root above, the expected Swagger UI location is:

```text
http://localhost:8080/template-web-service-java/docs/
```

The OpenAPI JSON endpoint depends on the REST/OpenAPI application configuration. An example endpoint is:

```text
http://localhost:8080/template-web-service-java/ws/openapi.json
```

> Verify that Swagger UI loads the correct OpenAPI JSON URL. The template's existing Swagger UI integration may require changes to its URL replacement logic, depending on the Swagger UI version.

## Included dependencies

The project declares optional building blocks for REST APIs, JWT authentication, reports, Office documents, email, and Jakarta Faces interfaces. Their presence in `pom.xml` does **not** mean that these features are automatically implemented or secured.

Remove the modules you do not need when starting a smaller project.

## Customization checklist

- [ ] Change Maven `groupId`, `artifactId`, `version`, and application name
- [ ] Rename Java packages and REST paths
- [ ] Update `openapi-configuration.json` package scanning, server URLs, title, and contact data where applicable
- [ ] Verify Swagger UI points to the correct OpenAPI endpoint
- [ ] Set up your application server and database resources if needed
- [ ] Configure authentication secrets outside source control
- [ ] Review dependency versions and security updates
- [ ] Remove IDE-specific server references from shared settings
- [ ] Run the build and test the deployed service

## Author

**takka-sama**  
GitHub: [@takkasama](https://github.com/takkasama)

## Repository

[github.com/takkasama/template-web-service-java-maven](https://github.com/takkasama/template-web-service-java-maven)
