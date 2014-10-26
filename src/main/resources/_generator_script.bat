cd "C:\Users\Mathias\git\jts\src\main\resources\bin"

:generate net-file
netgenerate --seed 88 --rand --rand.iterations 4 --rand.bidi-probability 1 --default.lanenumber 2 --no-internal-links --output-file "..\multilane.net.xml"

:generate routes file
activitygen --net-file "..\multilane.net.xml" --stat-file "..\multilane.stat.xml" --output-file "..\multilane.rou.xml" --random --duration-d 1
