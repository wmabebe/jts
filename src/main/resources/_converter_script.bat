cd "C:\Users\Mathias\git\jts\src\main\resources\bin"

:generate net-file
netconvert --osm "..\map1.osm" --output-file "..\map1.net.xml" --no-internal-links

:generate routes file
activitygen --net-file "..\map1.net.xml" --stat-file "..\map1.stat.xml" --output-file "..\map1.rou.xml" --random --duration-d 1

: