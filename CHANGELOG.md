# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.11.0 - 2020-06-17]
### Added
- collection permutations
- clipboard nrepl middleware

## [0.8.0 - 2019-02-13]
### Added
- tmp file utils: tempify, as-tmp, smart-delete, with-tmps

## [0.7.0 - 2018-12-19]
### Added
- string utils for extracting substrings between matching prefixes and suffixes

## [0.5.1 - 2018-11-19]
### Fixed
- short timeout of host-port-listening?

## [0.5.0 - 2018-11-15]
### Added
- with-tf macro to replace the (let [file (tmp-file)] (with-tmp-file file)) workflow

## [0.4.3 - 2018-08-06]
### Fixed
- bug in resource-as-folder-child when used from a jar file

## [0.4.1 - 2018-07-18]
### Added
- touch-tmp and zero-fill fn

### Fixed
- replaced "/" with a function that gets the OS's file separator

## [0.4.0 - 2018-07-12]
### Added
- More options for dealing with resources

## [0.3.1 - 2018-05-15]
### Fixed
- added (:gen-class) to the ns function calls

## [0.3.0 - 2018-03-06]
### Added
- a function for doing multiple replacements

## [0.2.0 - 2018-02-16]
### Added
- macro that will repeatedly execute the body until the timeout is reached or the body does not throw and exception

## [0.1.7 - 2018-01-25]
### Addded
- function for checking if a host port is listening

## [0.1.6 - 2018-01-25]
### Added
- file manipulation functions

## [0.1.5 - 2018-01-19]
### Added
- functions for dealing with loacl network interfaces

### Fixed
- resource-as-file function works properly now

## 0.1.4 - 2018-01-17
### Added
- generic wait function

## 0.1.2 - 2018-01-15
### Fixed
- namespace issue

## 0.1.1 - 2018-01-15
### Added
- function for recursively deleting a file/folder

## 0.1.0 - 2018-01-15
### Added
- io utils for tmp files/folders and resources
