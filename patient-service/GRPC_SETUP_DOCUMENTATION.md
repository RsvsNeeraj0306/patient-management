# gRPC Setup Documentation for Patient Service

## Overview
This document outlines all the dependencies, configurations, and setup required to add gRPC support to the Patient Service Spring Boot application.

## Dependencies Added to `pom.xml`

### 1. Core gRPC Dependencies

```xml
<!--GRPC Dependencies -->
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>1.69.0</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>1.69.0</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>1.69.0</version>
</dependency>
```

**Purpose:**
- `grpc-netty-shaded`: Provides the underlying network transport layer
- `grpc-protobuf`: Protocol Buffers support for gRPC
- `grpc-stub`: Generated stub classes for client-server communication

### 2. Java 9+ Compatibility

```xml
<dependency> <!-- necessary for Java 9+ -->
    <groupId>org.apache.tomcat</groupId>
    <artifactId>annotations-api</artifactId>
    <version>6.0.53</version>
    <scope>provided</scope>
</dependency>
```

**Purpose:** Required for Java 9+ compatibility with gRPC annotations.

### 3. Spring Boot gRPC Integration

```xml
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-spring-boot-starter</artifactId>
    <version>3.1.0.RELEASE</version>
</dependency>
```

**Purpose:** Provides Spring Boot auto-configuration for gRPC servers and clients.

### 4. Protocol Buffers Support

```xml
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>4.29.1</version>
</dependency>
```

**Purpose:** Core Protocol Buffers runtime library.

### 5. gRPC Services (Server Reflection)

```xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-services</artifactId>
    <version>1.69.0</version>
</dependency>
```

**Purpose:** Provides additional gRPC services including server reflection for tools like Postman.

## Build Configuration

### 1. OS Maven Plugin Extension

```xml
<extensions>
    <!-- Ensure OS compatibility for protoc -->
    <extension>
        <groupId>kr.motd.maven</groupId>
        <artifactId>os-maven-plugin</artifactId>
        <version>1.7.0</version>
    </extension>
</extensions>
```

**Purpose:** Detects the operating system and architecture for downloading the correct protoc binary.

### 2. Enhanced Maven Compiler Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>17</source>
        <target>17</target>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**Purpose:** Updated to explicitly specify Java 17 and maintain Lombok support.

### 3. Protobuf Maven Plugin

```xml
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.6.1</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:4.29.1:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.69.0:exe:${os.detected.classifier}</pluginArtifact>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Purpose:** 
- Compiles `.proto` files into Java classes
- Generates gRPC service stubs and client code
- Automatically runs during Maven compile phase

## Directory Structure Created

```
patient-service/
└── src/
    └── main/
        ├── java/
        │   └── com/pm/patient_service/grpc/
        │       └── [gRPC service implementations go here]
        └── proto/
            └── [.proto files go here]
```

## Proto File Location

Proto files should be placed in:
```
src/main/proto/
```

Example: `src/main/proto/patientservice.proto`

## Application Configuration

The following properties can be added to `application.properties`:

```properties
# gRPC Server Configuration
grpc.server.port=9090
grpc.server.address=0.0.0.0
grpc.server.reflection-service-enabled=true
```

**Configuration Details:**
- `grpc.server.port`: Port for gRPC server (separate from HTTP port)
- `grpc.server.address`: Bind address (0.0.0.0 for all interfaces)
- `grpc.server.reflection-service-enabled`: Enables server reflection for tools like Postman

## Generated Code Location

After compilation, generated Java classes will be in:
```
target/generated-sources/protobuf/java/
target/generated-sources/protobuf/grpc-java/
```

## Service Implementation

gRPC services should:
1. Extend the generated `*ImplBase` class
2. Be annotated with `@GrpcService`
3. Be placed in the `grpc` package

Example:
```java
@GrpcService
public class PatientGrpcService extends PatientServiceGrpc.PatientServiceImplBase {
    // Service implementation
}
```

## Testing

- **gRPC Port:** Default 9090 (configurable)
- **HTTP Port:** Default 4000 (existing Spring Boot app)
- **Tools:** Postman (with server reflection), grpcurl, BloomRPC

## Version Compatibility

- **Spring Boot:** 3.5.3
- **Java:** 17
- **gRPC:** 1.69.0
- **Protocol Buffers:** 4.29.1
- **gRPC Spring Boot Starter:** 3.1.0.RELEASE

## Build Commands

```bash
# Compile proto files and generate gRPC classes
mvn compile

# Clean and rebuild everything
mvn clean compile

# Package the application
mvn clean package
```

## Notes

1. **Proto file naming:** Use consistent naming (e.g., `patientservice.proto`)
2. **Package naming:** Set `java_package` option in proto files
3. **Port conflicts:** Ensure gRPC port doesn't conflict with HTTP port
4. **Server reflection:** Essential for Postman and other gRPC testing tools
