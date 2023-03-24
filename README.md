# Java Dependency Injection

![](https://img.shields.io/github/actions/workflow/status/ParadoxPixel/JaDI/maven.yml "build")
![](https://img.shields.io/github/license/ParadoxPixel/JaDI "license")
![](https://img.shields.io/github/v/release/ParadoxPixel/JaDI "release")

### Getting Started

Add the dependency to your project

```xml
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://www.jitpack.io</url>
    </repository>
  </repositories>

  <dependencies>
  	<dependency>
	    <groupId>com.github.paradoxpixel</groupId>
	    <artifactId>jadi</artifactId>
	    <version>1.0.2</version>
	  </dependency>
  </dependencies>
```

Then you can start using it by getting a new instance

```java
JaDI jadi = new JaDI();
...
CompletableFuture<MyClass> future=jadi.resolve(Type.of(MyClass.class));
```