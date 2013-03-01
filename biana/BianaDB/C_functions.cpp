#include <Python.h>
#include <iostream>
#include <fstream.h>

#include "hash_map.h"
#include <list>


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

static PyObject* PyPianaC_Unify(PyObject *self, PyObject *args, PyObject *kwargs){


  char *eq_file = NULL;
  char *all_list_file = NULL;
  char *unif_temp_file = NULL;

  static char *kwlist[] = {"temp_equivalences_file","temp_list_file","unification_temp_file",NULL};

  if( !PyArg_ParseTupleAndKeywords(args, kwargs, "sss", kwlist, &eq_file, &all_list_file, &unif_temp_file ) ){
    std::cerr << "Error parsing paramaters in unify C method\n";
    return Py_BuildValue("");
  }

  int current_userID = 1;

  int a;
  int b;
  int eq1;
  int eq2;

  __gnu_cxx::hash_map<int,int> hm1;

  __gnu_cxx::hash_map<int,std::list<int>*> userEntities_externalEntities_hash_list;
    

  ifstream filein;
  filein.open(eq_file, ios::in);

  
  while( filein >> eq1 ){

    filein >> eq2;
    
    a = hm1[eq1];
    b = hm1[eq2];

    if( !a && !b ){
      userEntities_externalEntities_hash_list[current_userID] = new std::list<int>();
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
	for( std::list<int>::iterator from = userEntities_externalEntities_hash_list[b]->begin(); 
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

  filein.open(all_list_file, ios::in);


  while( filein >> eq1 ){
    if( !hm1[eq1] ){
      hm1[eq1] = current_userID;
      current_userID++;
    }
  }
  		
  filein.close();


  /* Do the fusion between equivalent user entities */
  std::cerr << "Doing the fusion between equivalent external entities\n";

  __gnu_cxx::hash_map<int,int>::iterator p;
    
  /* Re-enumeration */
  std::cerr << "Re-enumerating\n";
  int new_user_entity_ID = 1;
  __gnu_cxx::hash_map<int,int> old_new_user_entities_hash;

  for( p = hm1.begin(); p!= hm1.end(); ++p ){
    if( !old_new_user_entities_hash[hm1[p->first]] ){
      old_new_user_entities_hash[hm1[p->first]] = new_user_entity_ID;
      new_user_entity_ID++;
    }
    hm1[p->first] = old_new_user_entities_hash[hm1[p->first]];
  }


  ofstream fileout;

  fileout.open(unif_temp_file, ios::out);


  std::cerr << "Inserting data to database...\n";
  
  for( p = hm1.begin(); p!= hm1.end(); ++p )
    fileout << hm1[p->first] << "\t" << p->first << std::endl;

  fileout.close();

  return Py_BuildValue("");

}



//==============================================================================
// Module init

static PyMethodDef functions[] = {
   // run.h
   { "unify", (PyCFunction)PyPianaC_Unify, METH_VARARGS|METH_KEYWORDS, "Unify" },

   { NULL }
};

extern "C" void initC_functions(void)
{
    PyObject *m;

    m = Py_InitModule3("C_functions", functions, "Piana compiled functions");

    if (!m)
       return;

}

static const char* _rcsid_module __attribute__((unused)) = "$Id: C_functions.cpp,v 1.3 2008/05/23 13:41:55 jgarcia Exp $";

