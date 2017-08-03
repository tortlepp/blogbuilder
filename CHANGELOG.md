# Changelog
All notable changes to BlogBuilder are documented in this file. The format is based on [Keep a Changelog](http://keepachangelog.com/).


## [Unreleased]
### Changed
- Refactoring of almost all classes; some moving, renaming and splitting of classes
- Updated build dependency: Freemarker 2.3.25-incubating to 2.3.26-incubating
- Updated check dependencies: PMD 5.5.0 to 5.8.1 and Checkstyle 7.0 to 8.1
- Changed Markdown parser from [pegdown](https://github.com/sirthias/pegdown) to [flexmark-java](https://github.com/vsch/flexmark-java)

### Removed
- Old sample project content based on various ipsum texts

### Added
- New sample project content based on [Cupcake Ipsum](http://www.cupcakeipsum.com/)
- Custom Gradle task for Javadoc to include private members
- Added a `package-info.java` to all packages


## [0.5] - 2016-07-23
### Added
- First public release of BlogBuilder


[Unreleased]: https://github.com/tortlepp/BlogBuilder
[0.5]: https://github.com/tortlepp/BlogBuilder/releases/tag/v0.5