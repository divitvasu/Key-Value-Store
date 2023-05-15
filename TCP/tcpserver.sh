PROJECT_NETWORK='host'
SERVER_IMAGE='servertcp'
SERVER_CONTAINER='server1'
CLIENT_IMAGE='clienttcp'
CLIENT_CONTAINER='client1'

# clean up existing resources, if any
echo "----------Cleaning up existing resources----------"
docker container stop $SERVER_CONTAINER 2> /dev/null && docker container rm $SERVER_CONTAINER 2> /dev/null
docker container stop $CLIENT_CONTAINER 2> /dev/null && docker container rm $CLIENT_CONTAINER 2> /dev/null
#docker network rm $PROJECT_NETWORK 2> /dev/null

# only cleanup
if [ "$1" == "cleanup-only" ]
then
  exit
fi

# create a custom virtual network
#echo "----------creating a virtual network----------"
#docker network create $PROJECT_NETWORK

# build the images from Dockerfile
echo "----------Building images----------"
docker build -t $CLIENT_IMAGE --target TCPclient-build .
docker build -t $SERVER_IMAGE --target TCPserver-build .

# run the image and open the required ports
echo "----------Running server app----------"
docker run --rm --name $SERVER_CONTAINER --network $PROJECT_NETWORK $SERVER_IMAGE java Server_TCP_app "$1"
