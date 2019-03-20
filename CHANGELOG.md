# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html). (Patch version X.Y.0 is implied if not specified.)

## [Unreleased]
### Added
- Added logging and debug statements
- Added Travis and Coveralls

### Changed
- AQCU Framework to 0.0.6-SNAPSHOT
- Exclude config classes from coveralls reporting

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

[Unreleased]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-lookups-0.0.4...master
[0.0.2]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-lookups-0.0.1...aqcu-lookups-0.0.2
[0.0.3]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-lookups-0.0.2...aqcu-lookups-0.0.3
[0.0.4]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-lookups-0.0.3...aqcu-lookups-0.0.4

