# ☁️ AWS Deployment Guide — MicroSpringBoot IoC Application Server

This guide provides a **complete step-by-step walkthrough** for deploying the MicroSpringBoot server on an **AWS EC2 instance**.

---

## 📋 Table of Contents

1. [Prerequisites](#-prerequisites)
2. [Step 1: Launch an EC2 Instance](#step-1-launch-an-ec2-instance)
3. [Step 2: Configure Security Group](#step-2-configure-security-group)
4. [Step 3: Connect to the Instance](#step-3-connect-to-the-instance)
5. [Step 4: Install Java and Maven](#step-4-install-java-and-maven)
6. [Step 5: Clone and Build the Project](#step-5-clone-and-build-the-project)
7. [Step 6: Run the Server](#step-6-run-the-server)
8. [Step 7: Test from Browser](#step-7-test-from-browser)
9. [Step 8: Run as Background Process](#step-8-run-as-background-process)
10. [Troubleshooting](#-troubleshooting)
11. [Cleaning Up](#-cleaning-up)

---

## 🔧 Prerequisites

Before you start, make sure you have:

| Requirement | Details |
|-------------|---------|
| **AWS Account** | A free-tier eligible AWS account at [aws.amazon.com](https://aws.amazon.com/) |
| **Key Pair (.pem)** | An SSH key pair to connect to the EC2 instance |
| **SSH Client** | Terminal (Linux/Mac) or PuTTY (Windows) or AWS EC2 Instance Connect |

---

## Step 1: Launch an EC2 Instance

1. Log in to the **AWS Management Console**
2. Navigate to **EC2** → **Instances** → **Launch Instance**
3. Configure the instance:

| Setting | Value |
|---------|-------|
| **Name** | `microspringboot-server` |
| **AMI** | Amazon Linux 2023 (or Ubuntu 22.04 LTS) |
| **Instance type** | `t3.micro` (free tier eligible) |
| **Key pair** | Select or create a key pair (download the `.pem` file) |
| **Network** | Default VPC, public subnet, auto-assign public IP: **Enabled** |

4. Click **Launch Instance**

> 📸 **Screenshot suggestion:** Take a screenshot of the EC2 launch confirmation page → save as `images/aws_ec2_launch.png`

---

## Step 2: Configure Security Group

The server listens on port **8080**, so you must allow inbound traffic on that port.

1. Go to **EC2** → **Security Groups**
2. Select the security group attached to your instance
3. Click **Edit inbound rules** → **Add rule**

| Type | Protocol | Port Range | Source | Description |
|------|----------|------------|--------|-------------|
| Custom TCP | TCP | 8080 | 0.0.0.0/0 | MicroSpringBoot HTTP |
| SSH | TCP | 22 | My IP | SSH access |

4. Click **Save rules**

> ⚠️ **Security Note:** In production, restrict port 8080 to specific IPs instead of `0.0.0.0/0`.

> 📸 **Screenshot suggestion:** Take a screenshot of the security group rules → save as `images/aws_security_group.png`

---

## Step 3: Connect to the Instance

### Option A: SSH from Terminal (Linux/Mac/Git Bash)

```bash
# Set correct permissions for the key file
chmod 400 your-key-pair.pem

# Connect to the instance
ssh -i "your-key-pair.pem" ec2-user@<EC2_PUBLIC_IP>
```

### Option B: EC2 Instance Connect (Browser-based)

1. Go to **EC2** → **Instances**
2. Select your instance → **Connect**
3. Choose **EC2 Instance Connect** → **Connect**

### Option C: PuTTY (Windows)

1. Convert `.pem` to `.ppk` using PuTTYgen
2. Open PuTTY:
   - Host: `ec2-user@<EC2_PUBLIC_IP>`
   - Connection → SSH → Auth → Browse for `.ppk` file
3. Click **Open**

> 📸 **Screenshot suggestion:** Take a screenshot of the SSH connection → save as `images/aws_ssh_connect.png`

---

## Step 4: Install Java and Maven

### Amazon Linux 2023

```bash
# Update the system
sudo dnf update -y

# Install Java 17
sudo dnf install java-17-amazon-corretto-devel -y

# Verify Java installation
java -version

# Install Maven
sudo dnf install maven -y

# Verify Maven installation
mvn -version
```

### Ubuntu 22.04

```bash
# Update the system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Verify Java installation
java -version

# Install Maven
sudo apt install maven -y

# Verify Maven installation
mvn -version
```

### Install Git (if not already installed)

```bash
# Amazon Linux
sudo dnf install git -y

# Ubuntu
sudo apt install git -y

# Verify
git --version
```

> 📸 **Screenshot suggestion:** Take a screenshot showing `java -version`,`mvn -version` and `git -version` output → save as `images/aws_java_version.png`, `images/aws_maven_version.png` and `images/aws_git_version.png`

---

## Step 5: Clone and Build the Project

```bash
# Clone the repository
git clone https://github.com/AnderssonProgramming/ioc-application-server.git

# Navigate to the project directory
cd ioc-application-server

# Build the project
mvn clean compile

# Run the tests to verify everything works
mvn test
```

Expected test output:

```
[INFO] Tests run: 124, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

> 📸 **Screenshot suggestion:** Take a screenshot of `mvn test` showing 124 tests passing → save as `images/aws_tests.png`

---

## Step 6: Run the Server

### Auto-scan mode (recommended)

```bash
mvn exec:java
```

### CLI mode (specific controller)

```bash
java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot co.edu.escuelaing.reflexionlab.demo.HelloController
```

You should see:

```
===============================================
   __  __ _                ____             _
  |  \/  (_) ___ _ __ ___ / ___| _ __  _ __(_)_ __   __ _
  | |\/| | |/ __| '__/ _ \\___ \| '_ \| '__| | '_ \ / _` |
  | |  | | | (__| | | (_) |___) | |_) | |  | | | | | (_| |
  |_|  |_|_|\___|_|  \___/|____/| .__/|_|  |_|_| |_|\__, |
                                 |_|                  |___/
          MicroSpringBoot v1.0 - IoC Application Server
===============================================
  Port: 8080
  Static files: /webroot
  Routes registered: 6
    GET /
    GET /hello
    GET /greeting
    GET /pi
    GET /square
    GET /time
-----------------------------------------------
  Server started at http://localhost:8080
===============================================
```

> 📸 **Screenshot suggestion:** Take a screenshot of the server startup banner → save as `images/aws_server_startup.png`

---

## Step 7: Test from Browser

Open your browser and navigate to your **EC2 public IP** on port 8080:

| URL | What it does |
|-----|-------------|
| `http://<EC2_PUBLIC_IP>:8080/` | API Explorer page |
| `http://<EC2_PUBLIC_IP>:8080/hello` | Hello World |
| `http://<EC2_PUBLIC_IP>:8080/greeting?name=Pedro` | Greeting with @RequestParam |
| `http://<EC2_PUBLIC_IP>:8080/pi` | Math.PI |
| `http://<EC2_PUBLIC_IP>:8080/square?n=7` | Square of 7 |
| `http://<EC2_PUBLIC_IP>:8080/time` | Current time |

> 📸 **Screenshot suggestion:** Take a screenshot of the browser showing the API Explorer on EC2 → save as `images/aws_deployment.png`

> 📸 **Screenshot suggestion:** Take a screenshot of `http://<EC2_PUBLIC_IP>:8080/greeting?name=Pedro` → save as `images/aws_greeting.png`

---

## Step 8: Run as Background Process

To keep the server running after you disconnect from SSH:

### Using `nohup`

```bash
# Run in background, output to nohup.out
nohup mvn exec:java &

# Verify it's running
curl http://localhost:8080/hello

# View logs
tail -f nohup.out

# Find and stop the process later
ps aux | grep MicroSpringBoot
kill <PID>
```

### Using `screen`

```bash
# Install screen
sudo dnf install screen -y   # Amazon Linux
sudo apt install screen -y   # Ubuntu

# Create a named session
screen -S microspringboot

# Run the server
mvn exec:java

# Detach from session: Ctrl+A then D

# Reattach later
screen -r microspringboot

# Stop: Ctrl+C inside the session
```

---

## 🔍 Troubleshooting

### Port 8080 not accessible from browser

1. **Check Security Group:** Ensure port 8080 is open for inbound TCP from `0.0.0.0/0`
2. **Check the server is running:** `curl http://localhost:8080/hello` from inside the EC2 instance
3. **Check firewall:** `sudo iptables -L` — ensure no rules block port 8080
4. **Check public IP:** Make sure you're using the **public** IP, not the private one

### Java not found

```bash
# Check if Java is installed
which java
java -version

# If not installed, install Java 17
sudo dnf install java-17-amazon-corretto-devel -y  # Amazon Linux
sudo apt install openjdk-17-jdk -y                   # Ubuntu
```

### Maven build fails

```bash
# Make sure you're in the project directory
pwd
# Should show: /home/ec2-user/ioc-application-server

# Clean and rebuild
mvn clean compile
```

### Connection refused

```bash
# Check if the process is running
ps aux | grep java

# Check if port 8080 is in use
sudo netstat -tlnp | grep 8080
# or
sudo ss -tlnp | grep 8080
```

---

## 🧹 Cleaning Up

To avoid AWS charges, **terminate** the EC2 instance when done:

1. Go to **EC2** → **Instances**
2. Select your instance
3. **Instance state** → **Terminate instance**
4. Confirm termination

> ⚠️ **Important:** Stopping the instance still incurs storage charges. **Terminate** to fully clean up.

---

## 📝 Summary of Commands

```bash
# On EC2 instance:
sudo dnf update -y
sudo dnf install java-17-amazon-corretto-devel maven git -y
git clone https://github.com/AnderssonProgramming/ioc-application-server.git
cd ioc-application-server
mvn clean compile
mvn test
mvn exec:java
```

---

## 📸 Screenshots Checklist

Take and add these screenshots to the `images/` directory:

| Screenshot | Filename | Description |
|------------|----------|-------------|
| EC2 Launch | `aws_ec2_launch.png` | EC2 instance creation page |
| Security Group | `aws_security_group.png` | Inbound rules showing port 8080 |
| SSH Connection | `aws_ssh_connect.png` | Terminal connected to EC2 |
| Java + Maven | `aws_java_version.png`,`aws_maven_version.png` and `aws_git_version.png` | `java -version`, `mvn -version` and `git --version` output |
| Tests Passing | `aws_tests.png` | `mvn test` showing 124 tests pass |
| Server Startup | `aws_server_startup.png` | MicroSpringBoot banner |
| Browser - API | `aws_deployment.png` | API Explorer on EC2 public IP |
| Browser - Greeting | `aws_greeting.png` | Greeting endpoint on EC2 |
