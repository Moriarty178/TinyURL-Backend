# Tiny URL Service

## 📌 Overview
......................

## 🔧 Features

### 1. **Lưu trữ ánh xạ short URL : long URL**
- Về cơ bản chúng ta có thể bắt đầu với việc sử dụng HashTable, và sử dụng hash function thuần túy. Nhưng đối với yêu cầu thiêt kế ứng dụng rút gọn URL để đáp ứng lượng lớn người dùng và yêu cầu thì việc sử dụng HashTable sẽ không khả thi.
- Giải pháp thay thế: Sử dụng cơ sở dữ liệu quan hệ (PostgreSQL) cho phần lưu trữ tương ứng shortUrl : longUrl
  + Bảng "url_table" bao gồm: id, short_url, long_url

## 🔼 System Enhancements & Optimizations

### 1. **Nginx as Load Balancer & Reverse Proxy (OpenResty)**
- **Load Balancing:** Distributes traffic across multiple backend instances, supporting auto-scaling based on system load.
- **Rate Limiting:** Implements IP-based request rate limiting using the **Sliding Window Logs** algorithm with Redis & Lua Script.
> Reference: [Nginx Notes](reverse_proxy_config/README.md)

### 2. **Database Optimization**
- **Database Replication:** Implements a **Primary-Replica** architecture with **Streaming Replication** to ensure high availability.
- **Connection Pooling:** Utilizes **Pgpool-II** to optimize PostgreSQL connection management, with additional configurations for failover and automatic primary node detection.
> Reference: [Database Notes](pg_primary_replica/README.md)

### 3. **Concurrency Control**
- Implements **Optimistic Locking** to handle data conflicts in a multi-threaded environment efficiently.
> Reference: [How does the entity version property work when using JPA and Hibernate](
https://vladmihalcea.com/jpa-entity-version-property-hibernate/)

## 🏗️ System Architecture

![image](https://i.imgur.com/LMZ5vME.png)

---

# Authors
- [@Moriarty178](https://github.com/Moriarty178)