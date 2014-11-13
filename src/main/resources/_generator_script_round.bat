cd "C:\Users\Mathias\git\jts\src\main\resources\bin"

:generate net-file
netgenerate --seed 88 --spider-net --spider-arm-number=10 --spider-circle-number=1 --spider-space-rad=200 --rand.bidi-probability 1 --default.lanenumber 2 --no-internal-links --output-file "..\round.net.xml"

:generate routes file
:activitygen --net-file "..\round.net.xml" --stat-file "..\round.stat.xml" --output-file "..\round.rou.xml" --random --duration-d 1
