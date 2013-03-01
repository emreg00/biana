from optparse import OptionParser

parser = OptionParser()

parser.add_option("-i","--input-file", dest="input_file",
                  help = "CD HIT clusters file", default="")

parser.add_option("-k","--clusters_x_file", dest="split_num",
		help = "Number of splits", default=10000)

parser.add_option("-p","--prefix", dest = "prefix",
		help = "Output prefix", default="clusters_")

(options, args) = parser.parse_args()


def split_cd_hit_clusters_file(clusters_file, prefix="clusters_",clusters_x_file=1000):

    clusters_file = open(clusters_file,'r')
    file_num = 1

    num_clusters=0

    out = open("%s%s" %(prefix,file_num),'w')

    for line in clusters_file:

        if line[0]=='>':
            num_clusters += 1
            if( num_clusters == clusters_x_file ):
                num_clusters=1
                out.close()
                file_num += 1
                out = open("%s%s" %(prefix,file_num),'w')

        out.write(line)

    out.close()


split_cd_hit_clusters_file(clusters_file=options.input_file, prefix=options.prefix,clusters_x_file=int(options.split_num))

