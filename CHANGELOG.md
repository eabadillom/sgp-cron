# Changelog

Todos los cambios importantes en este proyecto serán documentados en este archivo.

Este proyecto sigue versionado semántico (SemVer) y el formato de [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/).

---

## [1.2.0] - 2026-11-17
### Added
* Se agrega JobNoLaboral, una clase encargada de verificar si el día siguiente corresponde a un día no laborable. Si se cumple esta condición, obtiene los empleados vigentes de la empresa y ejecuta, para cada uno, el proceso de generación del registro de asistencia correspondiente. Si no es un día no laborable, omite el proceso.
* Se agrega SchedulerNoLaboral, una clase encargada de programar y ejecutar diariamente, en un horario determinado, el proceso de verificación y generación de registros de asistencia para días no laborables.
* Se agrega JobDiaDescanso, una clase encargada de verificar si el día siguiente es laborable. Si se cumple esta condición, obtiene los empleados vigentes de la empresa y ejecuta, para cada uno, el proceso de generación del registro de asistencia correspondiente a sus días de descanso. Si el día siguiente es no laborable, omite el proceso.
* Se agrega SchedulerDiaDescanso, una clase encargada de programar y ejecutar diariamente, en un horario determinado, el proceso de verificación y generación de registros de asistencia correspondientes a los días de descanso de cada empleado.

## [1.0.1] - 2026-04-17

### Added
* Se agregan archivos de documentación del proyecto (README.md, CHANGELOG.md y LICENSE).

### Changed
* Se actualiza dependencia `sgp-core` a la versión `0.1.1`.

### Removed
* Se elimina dependencia con `sgp-tools` debido a que `sgp-core` ya cuenta con ella.

## Nota
Los cambios previos a esta versión (desde el inicio del proyecto) no están documentados por tratarse de la fase inicial de desarrollo.
