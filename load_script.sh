#!/bin/bash

docker exec -it Cyclicality data/db/load_script.sh
docker exec -it Technology data/db/load_script.sh
docker exec -it GrowthValue data/db/load_script.sh
docker exec -it Inflation data/db/load_script.sh
docker exec -it InterestRates data/db/load_script.sh
docker exec -it WealthConcentration data/db/load_script.sh
