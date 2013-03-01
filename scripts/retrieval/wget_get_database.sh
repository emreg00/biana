if [ $# -eq 0 ]
then
    echo "Usage: $0 wget_<db_name>"
    exit 1
fi

len = "echo $1 | wc -c"

if [ $len -gt 255 ] # file name limit with ext2/3/4 format on *unix systems
then
    wget -i $1 -O $1.data
else
    wget -i $1 
fi

