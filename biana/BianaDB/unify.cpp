/*
    BIANA: Biologic Interactions and Network Analysis
    Copyright (C) 2009  Javier Garcia-Garcia, Emre Guney, Baldo Oliva

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

#include <iostream>
#include <fstream>

#include <list>

#include <cstring>

#if (__GNUC__ == 4 && __GNUC_MINOR__ > 2) 
    #include <backward/hash_map>
#else
    #include <ext/hash_map>
    using namespace __gnu_cxx;
#endif

using namespace std;

/*
This file contains the C implementation for computationally costly algorithms
*/

struct eqstr
{
  bool operator()(char* s1, char* s2) 
  {
    return strcmp(s1,s2) == 0;
  }
};

struct insensitive_eqstr
{
  bool operator()(char* s1, char* s2) 
  {
    while(*s1 && *s2){
      if( toupper((unsigned char)*s1) != toupper((unsigned char)*s2))
	return false;
      s1++;
      s2++;
    }
    return true;
  }
};

struct byte_eqstr
{
  bool operator()(char* s1, char* s2)
  {
    for( int i=0; i<16; i++ )
      if( s1[i] != s2[i] )
	return false;

    return true;
  }
};


int biana_unify( const char *eq_file, const char* all_list_file, const char* unif_temp_file)
{

  int current_userID = 1;

  int a;
  int b;
  int eq1;
  int eq2;

  hash_map<int,int> hm1;
  //__gnu_cxx::hash_map<int,int> hm1;

  hash_map<int,list<int>*> userEntities_externalEntities_hash_list;
  //__gnu_cxx::hash_map<int,list<int>*> userEntities_externalEntities_hash_list;
    

  ifstream filein;
  filein.open(eq_file, ios::in);

  
  while( filein >> eq1 ){

    filein >> eq2;
    
    a = hm1[eq1];
    b = hm1[eq2];

    if( !a && !b ){
      userEntities_externalEntities_hash_list[current_userID] = new list<int>();
      userEntities_externalEntities_hash_list[current_userID]->push_back(eq1);
      userEntities_externalEntities_hash_list[current_userID]->push_back(eq2);
      hm1[eq1] = current_userID;
      hm1[eq2] = current_userID;
      current_userID++;
    }
    else{
      if( !a ){
	hm1[eq1] = b;
	userEntities_externalEntities_hash_list[b]->push_back(eq1);
      }
      else if( !b ){
	userEntities_externalEntities_hash_list[a]->push_back(eq2);
	hm1[eq2] = a;
      }
      else if( a!=b ){
	// A and B are different and they must be similar...
	// It is necessary to change one of them by the other
	for( list<int>::iterator from = userEntities_externalEntities_hash_list[b]->begin(); 
	     from != userEntities_externalEntities_hash_list[b]->end();
	     ++from ){
	  hm1[*from] = a;
	}
	userEntities_externalEntities_hash_list[a]->merge(*userEntities_externalEntities_hash_list[b]);
	delete userEntities_externalEntities_hash_list[b];
	hm1[eq2] = a;
      }
    }
  }
  
  filein.close();

  /* Re-enumeration */
  cerr << "Re-enumerating\n";
  int new_user_entity_ID = 1;
  hash_map<int,int> old_new_user_entities_hash;
  //__gnu_cxx::hash_map<int,int> old_new_user_entities_hash;


  hash_map<int,int>::iterator p;
  //__gnu_cxx::hash_map<int,int>::iterator p;

  for( p = hm1.begin(); p!= hm1.end(); ++p ){
    if( !old_new_user_entities_hash[hm1[p->first]] ){
      old_new_user_entities_hash[hm1[p->first]] = new_user_entity_ID;
      new_user_entity_ID++;
    }
    hm1[p->first] = old_new_user_entities_hash[hm1[p->first]];
  }

  cerr << "Re-enumerated\n";

  ofstream fileout;

  fileout.open(unif_temp_file, ios::out);

  if( fileout == NULL ){
    cerr << "ERROR. Cannot open out file " << unif_temp_file << endl;
    return 1;
  }


  cerr << "Saving equivalences into file\n";
  for( p = hm1.begin(); p!= hm1.end(); ++p )
    fileout << hm1[p->first] << "\t" << p->first << endl;


  filein.open(all_list_file, ios::in);

  cerr << "Saving all into file\n";
  
  // Now, all external entities file is not stored in memory. It is writted into output while readed.
  // It generated memory problems with big databases, but now is solved doing it in this way
  while( filein >> eq1 ){
    if( hm1.find(eq1) == hm1.end() ){
      //hm1[eq1] = current_userID;
      fileout << new_user_entity_ID << "\t" << eq1 << endl;
      new_user_entity_ID++;
    }
    //cerr << hm1.size() << "\n";
    //fileout.flush();
  }

  cerr << "Finished";
  		
  filein.close();


  fileout.close();

  return 0;

}



int main(int argc, char* argv[1])
{
  if( argc != 4 )
    {
      cerr << "ERROR. 3 parameters required: equivalences_file all_list_file output_temp_file" << endl;
      return 1;
    }

  cerr << argv[1] << endl;
  cerr << argv[2] << endl;
  int to_return = biana_unify(argv[1], argv[2], argv[3]);

  cerr << "Finished";

  return to_return;

}

