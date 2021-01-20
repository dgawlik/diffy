# Diffy

Project is a Java implementation of [Meyer's](https://neil.fraser.name/writing/diff/myers.pdf) general
purpose diff algorithm. It compares encoded char arrays
and returns structure containing INSERT/DELETE/REPLACE/MATCH ranges.

**Features**:
* O(ND), N=sequences lengths, D=number of modifications
* adjusts offsets for surrogate pairs
* symbolic, or colorful output to console

### Usage

##### Compact

Diffed string is displayed as line with modifications.

```java
char[] source = "jooohnbb".toCharArray();
char[] target = "johnaa".toCharArray();

DiffInfo info = Diff.compute(source, target);
Printer p = Printer.from(info);
System.out.println(p.print());
```

Output
```shell
jo--[oo]hn~~[aa]
```

##### Verbose

Each modification broken down to single line with configurable context.

```java
char[] source = "jooohnbb".toCharArray();
char[] target = "johnaa".toCharArray();

DiffInfo info = Diff.compute(source, target);
Printer p = Printer.from(info).verbose();
System.out.println(p.print());
```

Output
```shell
*> ...jo--[oo]nbb...
*> ...ooohn~~[aa]
```

#### ANSI terminal colors

Using ANSI terminal escape characters to display colorful background.

```java
char[] source = "jooohnbb".toCharArray();
char[] target = "johnaa".toCharArray();

DiffInfo info = Diff.compute(source, target);
Printer p = Printer.from(info).withFormatter(new AnsiColorFormatter());
System.out.println(p.print());
```