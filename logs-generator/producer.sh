
cd /Users/amanoj1/Desktop/M.TECH/OGS_MTECH_SEM-3/SPA/Assignments/Assignment_2/Fake-Apache-Log_Generator/
while true
do
python apache-fake-log-gen.py -n 100 -o LOG

files=`ls access_log*.log`
#echo "$files"

for file in $files
do
echo $file
/usr/local/opt/kafka/bin/kafka-console-producer --broker-list localhost:9092 --topic spa < $file
mv $file produced/
done
sleep 120
done
