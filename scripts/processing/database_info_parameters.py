from optparse import OptionParser

parser = OptionParser()

parser.add_option("-n","--dbname", dest="dbname",
                  help = "")
parser.add_option("-s","--dbhost", dest="dbhost",
                  help = "", default = "localhost")
parser.add_option("-u","--dbuser", dest="dbuser",
                  help = "", default = "")
parser.add_option("-p","--dbpass", dest="dbpass",
                  help = "", default = "")

