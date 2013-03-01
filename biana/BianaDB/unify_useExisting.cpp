#include <iostream>
#include <fstream.h>

#include "hash_map.h"
#include <list>

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

  cerr << "Starting...\n";
  int current_max_userID = 1, current_userID = 1;


  int a;
  int b;
  int eq1;
  int eq2;

  __gnu_cxx::hash_map<int,int> hm1;

  __gnu_cxx::hash_map<int,list<int>*> userEntities_externalEntities_hash_list;

  list<int> unused_userEntity_id_list;
    

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
      //current_userID++;
      if(unused_userEntity_id_list.empty()) {
	  current_userID = ++current_max_userID;
      } else {
	  current_userID = unused_userEntity_id_list.front();
	  unused_userEntity_id_list.pop_front();
      }
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
	unused_userEntity_id_list.push_back(b);
	hm1[eq2] = a;
      }
    }
  }
  
  filein.close();

  cerr << "Equivalences file red\n";

  /*
  filein.open(all_list_file, ios::in);

  while( filein >> eq1 ){
    if( !hm1[eq1] ){
      hm1[eq1] = current_userID;
      current_userID++;
    }
  }
  		
  filein.close();
*/
  cerr << "All external entities in memory\n";

  /* Do the fusion between equivalent user entities */
  cerr << "Doing the fusion between equivalent external entities\n";

  __gnu_cxx::hash_map<int,int>::iterator p;
    
  /* Re-enumeration */
  /*
  cerr << "Re-enumerating\n";
  int new_user_entity_ID = 1;
  __gnu_cxx::hash_map<int,int> old_new_user_entities_hash;

  for( p = hm1.begin(); p!= hm1.end(); ++p ){
    if( !old_new_user_entities_hash[hm1[p->first]] ){
      old_new_user_entities_hash[hm1[p->first]] = new_user_entity_ID;
      new_user_entity_ID++;
    }
    hm1[p->first] = old_new_user_entities_hash[hm1[p->first]];
  }
  */

  ofstream fileout;

  fileout.open(unif_temp_file, ios::out);

  if( fileout == NULL ){
    cerr << "ERROR. Cannot open out file " << unif_temp_file << endl;
    return 1;
  }

  cerr << "Saving data\n";
  
  for( p = hm1.begin(); p!= hm1.end(); ++p )
    fileout << hm1[p->first] << "\t" << p->first << endl;

  //fileout.flush();
  //fileout.close();
  //fileout.open(unif_temp_file, ios::app);

  filein.open(all_list_file, ios::in);

  int counter = 0;
  while( filein >> eq1 ){
    //if( !hm1[eq1] ){
    if( hm1.find(eq1) == hm1.end() ){
      //hm1[eq1] = current_max_userID;
      fileout << current_max_userID << "\t" << eq1 << endl;
      current_max_userID++;
      counter++;
      //if(counter % 1000 == 0) {
	  //fileout.flush();
	  //fileout.close();
	  //filein.flush();
	  //fileout.open(unif_temp_file, ios::app);
      //}
    }
  }
  		
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

