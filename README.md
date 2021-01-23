# Diffy

![Download](https://img.shields.io/badge/version-1.1.0-blue)
![Download](https://img.shields.io/badge/coverage-89%25-blue)
![Download](https://img.shields.io/badge/PR's-welcome-green)

Project attempts to ease work on parsing
long char/byte sequences by providing diff functionality
in Java. At the core it is an implementation of **[Meyer's](https://neil.fraser.name/writing/diff/myers.pdf) general purpose diff
algorithm**. Comparisons return structure containing *insert|delete|replace|match* ranges.

### Features
Here are some tool's features:

* fast general purpose diff in nearly linear time for
  similar sequences
* readable representation of diff result either symbolic or
  colorful
* extensible and configurable
* minimal dependencies 
* implementation with ease of understanding in mind

### Installation

You need to first add [JCenter](https://stackoverflow.com/questions/44265547/how-to-properly-specify-jcenter-repository-in-maven-config) to your repositories. And then:

Maven
```xml
<dependency>
  <groupId>org.bytediff</groupId>
  <artifactId>diffy</artifactId>
  <version>1.1.0</version>
  <type>pom</type>
</dependency>
```

Gradle

```groovy
implementation 'org.bytediff:diffy:1.1.0'
```

### Usage

Let's start with comparing two strings:

```java
char[] source = "quickbrownfoxjumpingoverlazydog".toCharArray();
char[] target = "quickfoxjumpingoverlazydog".toCharArray();

DiffInfo info = Diff.compute(source, target);
String result = Printer.from(info).print();
System.out.println(result);
```
Console:
```shell
quick--[brown]foxjumpingoverlazydog
```
Comparing two arrays is results in *DiffInfo*. It holds various
methods for retrieving diff data programmatically. Currently
there is one *Printer* but it is fully configurable, more
on this in the next example.

According to console output to make source comparable to target we need to
delete word *brown*. Here *--[word]* stands for delete, *++[word]* for
insertion and *~~[word]* for replacement. A match prints sequence
unchanged.

When it comes to really long strings this output wouldn't be so handy.
There is a way to see **only** the modification
itself and with some surrounding context.

Here is the next example:
```java
char[] source = "quickbrownfoxjumpingoverlazydown".toCharArray();
char[] target = "quickfoxjumpingoverlazydown".toCharArray();

DiffInfo info = Diff.compute(source, target);
String result = Printer
    .from(info)
    .verbose()
    .withLeftContext(7)
    .withRightContext(4)
    .print();
System.out.println(result);
```
Console:
```shell
...brownfox++[jumping]over...
```

Now the output is one line per insert|delete|replace
with preceding 7 characters and followed by 4 characters.

If this symbolic representation is confusing there is an
option to display modifications in color.

Let's modify our first example:
```java
char[] source = "quickbrownfoxjumpingoverlazydown".toCharArray();
char[] target = "quickfoxjumpingoverlazydown".toCharArray();

DiffInfo info = Diff.compute(source, target);
String result = Printer
  .from(info)
  .withFormatter(new AnsiColorFormatter())
  .print();
System.out.println(result);
```


Lastly in case of working with raw bytes, much of the sequence wouldn't be printable. 
The next example encodes raw bytes to char[] and then displays their ordinals.

```java
byte[] source = new byte[]{1, 2, 3};
byte[] target = new byte[]{4, 2, 3};

char[] sourceC = Raw.bytesToChars(source);
char[] targetC = Raw.bytesToChars(target);

DiffInfo info = Diff.compute(sourceC, targetC);
Printer p = Printer
    .from(info)
    .withEncoding(new RawValueEncoder(10));
System.out.println(p.print());
```
Console:
```shell
~~[\4 ]\2 \3 
```

### Contributing

Feel free to raise an issue, submit 
PR or suggest improvement.