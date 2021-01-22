# Diffy

Project is a Java implementation of [Meyer's](https://neil.fraser.name/writing/diff/myers.pdf) general purpose diff
algorithm. It compares encoded char arrays and returns structure containing INSERT/DELETE/REPLACE/MATCH ranges.

**Features**:

* O(ND), N=sequences lengths, D=number of modifications
* adjusts offsets for surrogate pairs
* symbolic, or colorful output to console

### Installation

```groovy
repositories {
    mavenCentral()
    maven {
        url = 'https://dl.bintray.com/dgawlik/diffy/'
    }
}
```

```groovy
dependencies {
    ...
    implementation 'org.bytediff:diffy:1.0-ALPHA'
}

```

### Usage

##### Compact

Diffed string is displayed as line with modifications.

```java
char[] source="jooohnbb".toCharArray();
char[] target="johnaa".toCharArray();

DiffInfo diff=Diff.compute(source,target);
Printer p=Printer.from(diff);
System.out.println(p.print());
```

Output

```shell
jo--[oo]hn~~[aa]
```

##### Verbose

Each modification broken down to single line with configurable context.

```java
char[] source="jooohnbb".toCharArray();
char[] target="johnaa".toCharArray();

DiffInfo diff=Diff.compute(source,target);
Printer p=Printer.from(diff).verbose();
System.out.println(p.print());
```

Output

```shell
*> ...jo--[oo]nbb...
*> ...ooohn~~[aa]
```

##### ANSI terminal colors

Using ANSI terminal escape characters to display colorful background.

```java
char[] source="jooohnbb".toCharArray();
char[] target="johnaa".toCharArray();

DiffInfo diff=Diff.compute(source,target);
Printer p=Printer.from(diff).withFormatter(new AnsiColorFormatter());
System.out.println(p.print());
```

##### Raw bytes

With widening id conversion to char and printing character ordinal.

```java
byte[] source=new byte[]{1,2,3};
byte[] target=new byte[]{4,2,3};

char[] sourceC=Raw.bytesToChars(source);
char[] targetC=Raw.bytesToChars(target);

DiffInfo diff=Diff.compute(sourceC,targetC);
Printer p=Printer
    .from(diff)
    .withEncoding(new RawValueEncoder(10));
System.out.println(p.print());
```

Output

```shell
~~[\4 ]\2 \3
```

### Shortcuts

```java
DiffyShortcuts.log(sourceString,targetString);

DiffyShortcuts.logVerbose(sourceString,targetString);

DiffyShortcuts.logColors(sourceString,targetString);

DiffyShortcuts.log(sourceBytes,targetBytes,radix);

DiffyShortcuts.logVerbose(sourceBytes,targetBytes,radix);

DiffyShortcuts.logColor(sourceBytes,targetBytes,radix);

DiffyShortcuts.assertEquals(sourceString,targetString);

DiffyShortcuts.assertEquals(sourceBytes,targetBytes,radix);

```