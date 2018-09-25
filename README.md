[![Build Status](https://travis-ci.org/CDCgov/fdns-java-sdk.svg?branch=master)](https://travis-ci.org/CDCgov/fdns-java-sdk)

# FDNS Java SDK
This is the repository with the Java SDK for Foundation Services (FDNS).

## Usage
This library is designed to be used with FDNS as helpers to connect to other services.

Add this into your `pom.xml` to start using the Java SDK:

```
<!-- add the dependency to your project -->
<dependency>
  <groupId>gov.cdc</groupId>
  <artifactId>fdns-java-sdk</artifactId>
  <version>1.0.1</version>
</dependency>

<!-- add GitHub CDCgov to repositories -->
<repositories>
  <repository>
    <id>github-cdcgov</id>
    <url>https://github.com/CDCgov/maven-repository/raw/master/</url>
  </repository>
</repositories>
```

### Helpers

* `AbstractHelper`: An abstract helper which gets extended by the other helpers
* `AbstractMessageHelper`: A helper for tracing output
* `CDAHelper`: This is a helper for interfacing with fdns-ms-cda-utils
* `CombinerHelper`: This is a helper for interfacing with fdns-ms-combiner
* `DatabaseHelper`: This is a helper for inserting into a SQL database
* `HL7Helper`: This is a helper for interfacing with fdns-ms-hl7-utils
* `IndexingHelper`: This is a helper for interfacing with fdns-ms-indexing
* `MicrosoftHelper`: This is a helper for interfacing with fdns-ms-msft-utils
* `OAuthHelper`: This is a helper for interfacing with an OAuth 2 provider
* `ObjectHelper`: This is a helper for interfacing with fdns-ms-object
* `RequestHelper`: This is a helper for HTTP requests, used by many of the other helpers
* `ResourceHelper`: This is a helper for accessing application resources such as configuration properties
* `ScopesHelper`: This is a helper for interfacing with fdns-ms-scopes
* `StorageHelper`: This is a helper for interfacing with fdns-ms-storage

## Public Domain
This repository constitutes a work of the United States Government and is not
subject to domestic copyright protection under 17 USC § 105. This repository is in
the public domain within the United States, and copyright and related rights in
the work worldwide are waived through the [CC0 1.0 Universal public domain dedication](https://creativecommons.org/publicdomain/zero/1.0/).
All contributions to this repository will be released under the CC0 dedication. By
submitting a pull request you are agreeing to comply with this waiver of
copyright interest.

## License
The repository utilizes code licensed under the terms of the Apache Software
License and therefore is licensed under ASL v2 or later.

The source code in this repository is free: you can redistribute it and/or modify it under
the terms of the Apache Software License version 2, or (at your option) any
later version.

The source code in this repository is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the Apache Software License for more details.

You should have received a copy of the Apache Software License along with this
program. If not, see https://www.apache.org/licenses/LICENSE-2.0.html

The source code forked from other open source projects will inherit its license.


## Privacy
This repository contains only non-sensitive, publicly available data and
information. All material and community participation is covered by the
Surveillance Platform [Disclaimer](https://github.com/CDCgov/template/blob/master/DISCLAIMER.md)
and [Code of Conduct](https://github.com/CDCgov/template/blob/master/code-of-conduct.md).
For more information about CDC's privacy policy, please visit [http://www.cdc.gov/privacy.html](http://www.cdc.gov/privacy.html).

## Contributing
Anyone is encouraged to contribute to the repository by [forking](https://help.github.com/articles/fork-a-repo)
and submitting a pull request. (If you are new to GitHub, you might start with a
[basic tutorial](https://help.github.com/articles/set-up-git).) By contributing
to this project, you grant a world-wide, royalty-free, perpetual, irrevocable,
non-exclusive, transferable license to all users under the terms of the
[Apache Software License v2](http://www.apache.org/licenses/LICENSE-2.0.html) or
later.

All comments, messages, pull requests, and other submissions received through
CDC including this GitHub page are subject to the [Presidential Records Act](https://www.archives.gov/about/laws/presidential-records.html)
and may be archived. Learn more at [http://www.cdc.gov/other/privacy.html](https://www.cdc.gov/other/privacy.html).

## Records
This repository is not a source of government records, but is a copy to increase
collaboration and collaborative potential. All government records will be
published through the [CDC web site](https://www.cdc.gov).

## Notices
Please refer to [CDC's Template Repository](https://github.com/CDCgov/template)
for more information about [contributing to this repository](https://github.com/CDCgov/template/blob/master/CONTRIBUTING.md),
[public domain notices and disclaimers](https://github.com/CDCgov/template/blob/master/DISCLAIMER.md),
and [code of conduct](https://github.com/CDCgov/template/blob/master/code-of-conduct.md).
