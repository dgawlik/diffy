# Diffy

Diffy is Java implementation of [Meyer's](https://neil.fraser.name/writing/diff/myers.pdf) general
purpose diff algorithm. 

**Features**:
* O(ND), N=sequences lengths, D=number of modifications
* supports multiple charsets, including UTF-16 surrogate pairs
* console coloring

### Usage

```java
byte[] source = "aJohnDoe".getBytes(StandardCharsets.UTF_8);
byte[] target = "aBBBBDoe".getBytes(StandardCharsets.UTF_8);

Diff d = new Diff();
DiffInfo info = d.compute(source, target, StandardCharsets.UTF_8);
System.out.println(PrettyDisplay.from(info).display());
```