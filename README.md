# GeoService
This service provide some basic functionality for GeoTags. It have next API:

1. **/checkUserLocation**: how far is user from his mark
2. **/addUserMark**: add new UserMark
3. **/updateUserMark**: update UserMark or remove it
4. **/tileStat**: shows GeoTile statistics (how many UserMarks is there)

**limitation**: user can have only one mark (easy to fix)

## Setup 
To start the program you should have `sbt` and scala with java installed.

### DataGenerator parameters
  1. **users-count** - number of UserMarks to generate (remember one user - one mark)
  2. **user-marks-path** - path where file with UserMarks will be saved (in `csv` format)
  3. **geo-tiles-count** - number of GeoTiles to generate (maximum value = `360 * 180`)
  4. **max-tile-error** - every GeoTile have measurement error inside it, this value is upper bound for it
  5. **geo-tiles-path** - path where file with GeoTiles will be saved (in `csv` format)

## Run (from SBT)
To run the program you need to generate Data first. To do it execute change `data-generator` part in `application.conf` file and execute `sbt generateData` task.
Now you have two files:

1. File with data about UserMarks (in `csv` format)
2. File with data about GeoTiles (in `csv` format)

To start service you need to specify paths to those files:
`sbt "run "{full-path-to-UserMarks.csv-file}" "{full-path-to-GeoTiles.csv-file}""`

## Run (from JAR)
It is possible to create Fat Jar to run service without SBT. To do it execute `sbt assembly` command.

To generate Data execute next command: `java -cp {path-to-jar} com.pronvis.onefactor.test.GenerateData {users-count} "{full-path-to-save-UserMarks.csv-file}" {geo-tiles-count} {max-tile-error} "{full-path-to-save-GeoTiles.csv-file}"`

To start service: `java -jar {path-to-jar} "{full-path-to-UserMarks.csv-file}" "{full-path-to-GeoTiles.csv-file}"`
  
## Test
To start tests execute `sbt test` in root project folder