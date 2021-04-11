
curl -s localhost:8081/reset
curl -s localhost:8082/reset

curl -s "http://localhost:8080/signIn?cabId=102&initialPos=30"
curl -s "http://localhost:8080/signIn?cabId=101&initialPos=30"

for i in $(cat in.txt); do

	rideId1=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=10&destinationLoc=100")
	if [ "$rideId1" != "-1" ];
	then
        	echo "Ride by customer 201 started"
	else
        	echo "Ride to customer 201 denied"
        	        testPassed="no"
	fi

	rideId2=$(curl -s "http://localhost:8081/requestRide?custId=202&sourceLoc=10&destinationLoc=110")
	if [ "$rideId" != "-1" ];
	then
        	echo "Ride by customer 202 started"
	else
        	echo "Ride to customer 202 denied"
        	        testPassed="no"
	fi

	resp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=$rideId1")
	echo $resp
	if [ "$resp" = "true" ];
	then
        	echo $rideId1 " has ended"
	else
        	echo "Could not end" $rideId1
        	testPassed="no"
	fi
	resp=$(curl -s "http://localhost:8080/rideEnded?cabId=102&rideId=$rideId1")
        echo $resp
        if [ "$resp" = "true" ];
        then
                echo $rideId2 " has ended"
        else
                echo "Could not end" $rideId2
                testPassed="no"
        fi

done
