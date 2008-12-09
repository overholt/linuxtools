/* profileimage - A class which represents a single image for
   which oprofile has samples (or for which some child dependency
   has samples).
   Written by Keith Seitz <keiths@redhat.com>
   Edited by Kent Sebastian     <ksebasti@redhat.com>
   Copyright 2004,2008 Red Hat, Inc.

   This program is open source software licensed under the Eclipse
   Public License ver. 1.

   Alternatively, this program may be used under the terms of the GNU
   General Public License as published by the Free Software Foundation;
   either version 2 of the License, or (at your option) any later
   version.  */

#ifndef _PROFILEIMAGE_H
#define _PROFILEIMAGE_H

#include <list>
#include <string>
#include <set>

#include "samplefile.h"

#define VDSO_NAME_STRING "[vdso]"

class sample;
class imageheader;

class profileimage
{
 public:
  // Constructor - pass in the samplefile; CANNOT BE NULL.
  profileimage (samplefile* sfile);

  // Destructor
  ~profileimage ();

  // Returns the name of this image; it is the name of the actual binary
  // in which samples were collected.
  std::string get_name (void) const;

  // Returns the image header for this image
  const imageheader* get_header (void);

  // Add the given profileimage as a dependency of this image
  void add_dependency (profileimage* image);

  // Returns a list of all the dependencies of this image
  std::list<profileimage*>* get_dependencies (void) const { return _dependencies; };

  // Returns the Oprofile samplefile for this image
  samplefile* get_samplefile (void) const { return _samplefile; };

  // Returns the count of all the samples collected in this image, excluding dependencies
  long get_count (void) const;

 private:
  // The samplefile (non-NULL)
  samplefile* _samplefile;

  // List of dependencies
  std::list<profileimage*>* _dependencies;

  // Image header
  imageheader* _header;
};

struct depimage_comp {
  bool operator() (const profileimage* lhs, const profileimage* rhs) { return (lhs->get_count() == rhs->get_count() ? true : lhs->get_count() > rhs->get_count() ); }
};


std::ostream& operator<< (std::ostream& os, profileimage* image);
void add_sample(std::list<sample*> &samples, sample* new_sample);
long get_dependent_count(const std::list<profileimage*>* const deps);
std::set<profileimage*, depimage_comp>* sort_depimages(const std::list<profileimage*>* const deps);
std::string get_name(const profileimage* p);
#endif // !_PROFILEIMAGE_H