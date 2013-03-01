# For creating source distribution: python setup.py sdist
#
# If you have administration privileges execute: python setup.py install
# If not use: python setup.py install --install-purelib=<dir_to_be_installed>
# If neither works for you try: python setup.py build and configure PYTHONPATH

from distutils.core import setup #, Extension
import os, stat, sys

# Build prerequired packages
# NetworkX
dir = os.getcwd()
os.chdir("ext/networkx") # networkx-0.99 now symbolic link
os.system("python setup.py build --build-lib=../../src/biana/ext")

# MySQLdb
os.chdir(dir)
os.chdir("ext/mysqldb") 
os.system("python setup.py build --build-lib=../../src/biana/ext")
os.system("mv ../../src/biana/ext/_mysql.so ../../src/biana/ext/MySQLdb/")

# Build/install BIANA
os.chdir(dir)

# For non-windows builds, compile unification C++ program
if sys.argv[1] == "build":
    if os.name == "posix" or os.name=="mac":
        os.system("g++ src/biana/BianaDB/unify.cpp -o src/biana/BianaDB/unify")
    else:
	print "\nYou may need to compile src/biana/BianaDB/unify.cpp for your system!\n Windows users: use self-installer package instead.\n"

# BIANA package
setup( name = 'biana',
       version = '1.1',
       description = "Biologic Interactions And Network Analysis",
       author = 'Javier Garcia-Garcia & Emre Guney',
       author_email= 'jgarcia1@imim.es',
       url = 'sbi.imim.es/web/BIANA.php',
       license = "GNU License",
       packages = ['biana',
                   'biana.BianaDB',
                   'biana.BianaObjects',
                   'biana.BianaParser',
                   'biana.utilities',
                   'biana.ext',
                   'biana.ext.networkx',
                   'biana.ext.MySQLdb',
                   'biana.ext.MySQLdb.constants',
                   'biana.ext.networkx.generators',
                   'biana.ext.networkx.drawing',
                   'biana.ext.networkx.readwrite',
                   'biana.ext.networkx.algorithms',
                   'biana.ext.networkx.algorithms.traversal',
                   'biana.ext.networkx.algorithms.isomorphism',
                   'biana.ext.networkx.classes',
                   'biana.ext.networkx.linalg',
                   'biana.ext.networkx.tests'],

       package_dir = {'': 'src'+os.sep},
       package_data = {"biana": ["BianaDB/unify", "BianaDB/win_unify.exe","ext/MySQLdb/_mysql.so"]} )
       #ext_package = 'biana.BianaDB',
       #ext_modules = [Extension('C_functions', 
       #                         #include_dirs = ['/usr/include/mysql'],
       #                         #libraries = ['mysqlclient'],
       #                         #library_dirs = ['/usr/lib/mysql'],
       #               		sources = [os.sep.join(['src','biana','BianaDB','unify.cpp'])])] )


def print_notice():
    print """
        \n!!! Important Notice !!!\n 
        You have to: \n
        \t1- give execute permission to build/lib<.sys_dep_name>/biana/BianaDB/unify\n
        \t2- add the folder containing biana (build/lib.<sys_dep_name>) into PYTHONPATH\n
        """

# Give execution permissions to unify if installing
if sys.argv[1] == "install":
    try:
	if os.name == "posix" or os.name=="mac": 
	    from biana import __path__ as biana_path
	    #os.fchmod(biana_path+os.sep+"BianaDB"+os.sep+"unify","+x")
	    os.chmod(biana_path[0]+os.sep+"BianaDB"+os.sep+"unify",stat.S_IXOTH) #stat.S_IEXEC)
	else: #os.chmod can not change execution flag in windows
	    print_notice()
    except: # install w/ root permissions
        pass
# Warn user that he has to give execution permissions to unify and make proper path settings
elif sys.argv[1] == "build":
    print_notice()

