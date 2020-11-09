# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html). (Patch version X.Y.0 is implied if not specified.)

## [Unreleased]

## [0.2.0] - 2020-11-10
### Added
- Role-based authorization for folder/report configuration CRUD operations
- Enforce unique report type configurations per folder
- Folder properties configurations so that local data managers can control permissions for the folder
- Audit information to report configuration object
- OAuth2 security

### Changed
- Dependencies are pulled from and pushed to WMA Artifactory
- Update aqcu-framework to version 0.0.10

### Removed
- Dependency on travis.ci

## [0.1.0] - 2020-02-14
### Added
- Added logging and debug statements
- Added Travis and Coveralls
- Docker configuration

### Changed
- AQCU Framework to 0.0.8-SNAPSHOT
- Exclude config classes from coveralls reporting
- Merged this repository with the docker-aqcu-lookups repository
- Added CRUD functionality for report configurations stored in CHS S3 to support new user interface
- Upgrade to Springboot 2.2.0
- Use Springfox to generate Swagger UI.
- Fix Critical and High security vulnerabilities.

## [0.0.4] - 2019-02-20
### Added
- Enabled TLSv1.2
- Added default Aquarius timeout
- List of control conditions added to application yml

### Changed
- Disabled TLS1.0/1.1 by default
- Update to AQ SDK 18.8.1
- Update code to accommodate Control Conditions from yml rather than enum
- AQCU Framework to 0.0.5

## [0.0.3] - 2018-08-31
### Changed 
- AQ SDK Version to 18.6.2
- AQCU Framework to 0.0.3

## [0.0.2] - 2018-07-13
### Changed
- Fix formatting of service output for Control Conditions

## [0.0.1] - 2018-06-15
### Added 
- Initial release
- Correction List Service
- Downchain ProcessorList Service
- Grade Lookup Service
- Rating Curve List Service
- Time Series Data Corrected Service
- Time Series Description List Service
- Upchain Processor List Service

[Unreleased]: https://github.com/USGS-CIDA/aqcu-lookups/compare/0.2.0...master
[0.2.0]: https://github.com/USGS-CIDA/aqcu-lookups/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-lookups-0.0.4...0.1.0
[0.0.4]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-lookups-0.0.3...aqcu-lookups-0.0.4
[0.0.3]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-lookups-0.0.2...aqcu-lookups-0.0.3
[0.0.2]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-lookups-0.0.1...aqcu-lookups-0.0.2
