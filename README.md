# JSword - Free Bible Study Software

[![Build Status](https://github.com/crosswire/jsword/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/crosswire/jsword/actions)
[![JitPack](https://jitpack.io/v/crosswire/jsword.svg)](https://jitpack.io/#crosswire/jsword)

**JSword** is a Java library for Bible study software, providing a rich API for accessing, searching, and displaying biblical texts and related resources. It is the engine behind a variety of Bible study applications, offering a flexible and extensible platform for developers and users alike.

---

## Overview

JSword aims to make the Bible and related texts freely available in a wide range of languages and translations. It supports a variety of modern and ancient Bible versions, commentaries, dictionaries, daily devotionals, and other resources.

JSword powers several open-source projects, including [BibleDesktop](https://github.com/crosswire/bibledesktop) and [And Bible](https://github.com/AndBible/and-bible). The library is designed for cross-platform use and can be integrated into both desktop and mobile applications.

---

## Features

- **Read and Search Bible Texts**: Access a wide variety of Bibles, commentaries, dictionaries, and books in many languages.
- **Advanced Search**: Flexible searching by keywords, phrases, or passages, supporting Boolean logic and regular expressions.
- **Internationalization (i18n)**: Supports multiple languages for both interface and content, including community-provided translations.
- **Modular Design**: Easily extendable to support new resource types or front-end applications.
- **Sword Module Support**: Reads and manages [SWORD Project](https://crosswire.org/sword) modules created by CrossWire and others.
- **Strong’s Number Support**: Lookup and cross-reference original language words.
- **Daily Devotionals**: Built-in support for reading devotionals by date.
- **Open Source**: Freely available under the LGPL license.

---

## Getting Started

### Requirements

- **Java 8**
- **Gradle** (for building from source)

### Building from Source

```bash
git clone https://github.com/crosswire/jsword.git
cd jsword
./gradlew build
```

### Using JSword in Your Project

You can include JSword as a dependency via [JitPack](https://jitpack.io/#crosswire/jsword):

**Gradle:**
```gradle
implementation 'com.github.crosswire:jsword:master-SNAPSHOT'
```
Or replace `master-SNAPSHOT` with a tagged release version.

---

## Documentation

- [API Documentation (Javadoc)](https://javadoc.io/doc/org.crosswire.jsword/jsword)
- [Project Homepage](http://crosswire.org/jsword)
- [Module Repository](http://crosswire.org/sword/modules)
- [Wiki & User Guides](https://github.com/crosswire/jsword/wiki)

---

## Example Usage

```java
import org.crosswire.jsword.book.*;
import org.crosswire.jsword.passage.*;
import org.crosswire.jsword.versification.*;

Book bible = Books.installed().getBook("KJV");
Key key = bible.getKey("John 3:16");
BookData data = new BookData(bible, key);
String text = OSISUtil.getCanonicalText(data.getOsisFragment());
System.out.println(text);
```

---

## Contributing

We welcome contributions! See our [Contributing Guide](CONTRIBUTING.md) for more information.

- **Bug Reports & Feature Requests**: Please use [GitHub Issues](https://github.com/crosswire/jsword/issues) or [CrossWire Tracker](https://tracker.crosswire.org/projects/JS).
- **Pull Requests**: Fork the repo, create a branch, and submit a PR.

## Internationalization (i18n) and Translations

JSword supports multiple languages for both interface and content. Community-provided translations—especially of Bible book names (BibleNames)—are maintained through [Transifex](https://www.transifex.com/crosswire/jsword/).

### Contribute a Translation

We welcome contributions to our translations!  
If you would like to help translate Bible book names or other strings:

1. Visit our [Transifex project page](https://www.transifex.com/crosswire/jsword/).
2. Sign up or log in to Transifex.
3. Request to join a language team or submit new translations.

Translations are integrated regularly to keep JSword multilingual.


---

## Community & Support

- [CrossWire Forums](https://community.crosswire.org)
- [Mailing Lists](http://crosswire.org/mailman/listinfo)
- [GitHub Discussions](https://github.com/crosswire/jsword/discussions)

---

## License

JSword is released under the [LGPL 2.1 or later License](LICENSE).

---

## Acknowledgments

JSword is part of the [CrossWire Bible Society](http://crosswire.org) family of open-source Bible software. Many thanks to all contributors and the wider community!

---

For more information, visit the [JSword Project Page](http://crosswire.org/jsword).
