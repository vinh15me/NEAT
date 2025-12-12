# NEAT

**NEAT** is a lightweight and flexible file-sorting program that uses
user-defined rulesets to organize files automatically. It supports both
**absolute** and **relative** paths, and allows filtering files using
intuitive patterns such as:

-   Match file extensions → `.*\.txt`
-   Match filename beginnings → `a.*` (replace `a` with any starting
    sequence)

This guide explains **how to install dependencies, configure Java,
install Gson, compile, and run the NEAT GUI**.

------------------------------------------------------------------------

## ⭐ Features

-   Sorts files based on customizable patterns
-   Supports regex-style rules
-   Accepts both absolute and relative paths
-   Simple GUI interface
-   Minimal dependencies

------------------------------------------------------------------------

## 📦 Installation & Setup Guide (Ubuntu / WSL / Linux)

Follow the steps below to install **Oracle JDK 25**, configure your Java
environment, install **Gson**, and run NEAT.

------------------------------------------------------------------------

## 1️⃣ **Clone the Repository**

``` bash
git clone https://github.com/vinh15me/NEAT.git
```

------------------------------------------------------------------------

## 2️⃣ **Install Oracle JDK 25**

``` bash
wget https://download.oracle.com/java/25/latest/jdk-25_linux-x64_bin.tar.gz
sudo mkdir -p /usr/lib/jvm
sudo tar -xvzf jdk-25_linux-x64_bin.tar.gz -C /usr/lib/jvm
```

Register Java:

``` bash
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/jdk-25.0.1/bin/java 1
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/jdk-25.0.1/bin/javac 1
sudo update-alternatives --set java /usr/lib/jvm/jdk-25.0.1/bin/java
sudo update-alternatives --set javac /usr/lib/jvm/jdk-25.0.1/bin/javac
```

Verify:

``` bash
java --version
javac --version
```

Clean up:

``` bash
rm jdk-25_linux-x64_bin.tar.gz
```

------------------------------------------------------------------------

## 3️⃣ **Install Gson Library**

``` bash
sudo apt-get update
sudo apt-get install libgoogle-gson-java
```

------------------------------------------------------------------------

## 4️⃣ **Add Gson to Classpath**

``` bash
sudo vi /etc/environment
```

Add:
``` bash
CLASSPATH=".:/usr/share/java/gson.jar"
```

Reload:

``` bash
source /etc/environment
export CLASSPATH=".:/usr/share/java/gson.jar"
```

------------------------------------------------------------------------

## 5️⃣ **Compile and Run NEAT**

``` bash
cd NEAT
javac GUI.java
java GUI
```

------------------------------------------------------------------------

# 📘 Usage Notes

### ✔ Sorting by file extension

    .*\.txt

### ✔ Sorting by prefix

    a.*

### ✔ Paths

Both absolute and relative paths are supported.

------------------------------------------------------------------------

## ❤️ Contributing

Pull requests and suggestions are welcome.

