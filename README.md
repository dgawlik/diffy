# Diffy

![Download](https://img.shields.io/badge/version-1.1.0-blue)
![Download](https://img.shields.io/badge/coverage-89%25-blue)
![Download](https://img.shields.io/badge/PR's-welcome-green)

Project is attempt to ease work on parsing
long char/byte sequences. It does that by providing diff functionality
in Java. Under the hood it is an implementation of **[Meyer's](https://neil.fraser.name/writing/diff/myers.pdf) general purpose diff
algorithm**. Comparisons return structure containing *insert|delete|replace|match* ranges.

### Features
Here are some tool's features:

* fast general purpose diff in nearly linear time for
  similar sequences
* readable representation of diff result either symbolic or
  colorfull 
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

So you want to compare two strings...

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
methods for retrieving diff result programatically. Currently
there is one *Printer* but it is fully configurable, more
on this in the next example.

We can see that to make source comparable to target we need to
delete word *brown*. *--[word]* stands for delete, *++[word]* for
insertion and *~~[word]* for replacement. Match prints sequence
unchanged.

This is somewhat helpful but what if you had really long string?
Then only thing you would want to see is **only** the modification
itself, possibly with some context.

Here is our next example:
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

Now the output is one line for insert|delete|replace
with preceding 7 characters and followed by 4 characters.

But this symbolic representation can be confusing what if
we could have it color printed?

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


Lastly when working with raw bytes, we don't care what the string 
looks like because much of our sequnce won't be printable. So we 
need to encode somehow raw bytes to char[] and then compare them.

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

If you would like to raise an issue, submit 
PR or suggest improvement feel free to do so.