# tadpole

#### 使用方式
pom.xml文件中加入私有仓库地址
```xml
<repositories>
    <repository>
        <id>rdc-releases</id>
        <url>https://repo.rdc.aliyun.com/repository/75197-release-TiDTT0/</url>
    </repository>
    <repository>
        <id>rdc-snapshots</id>
        <url>https://repo.rdc.aliyun.com/repository/75197-snapshot-xdmzwv/</url>
    </repository>
</repositories>
```
添加依赖
```xml
<dependency>
	<groupId>studio.littlefrog.tadpole</groupId>
	<artifactId>tadpole</artifactId>
	<version>${tadpole.version}</version>
</dependency>
```