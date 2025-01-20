# Sử dụng image chứa JDK 17 hoặc phiên bản phù hợp với ứng dụng của bạn để build
FROM maven:3.8.4-openjdk-17-slim AS build

# Thư mục làm việc trong container
WORKDIR /app

# Copy mã nguồn từ máy chủ vào container
COPY . /app

# Build ứng dụng bằng Maven
RUN mvn clean install -DskipTests

# Sử dụng image chứa JDK để chạy ứng dụng
FROM eclipse-temurin:17-jdk-alpine

# Thêm thông tin metadata
LABEL maintainer="hieunm@gmail.com"
LABEL description="Sell Course"

# Thư mục làm việc trong container
WORKDIR /app

# Copy file JAR đã build từ bước build vào container
COPY --from=build /app/target/*.jar app.jar

# Expose cổng (phải khớp với cổng ứng dụng)
EXPOSE 8081

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]