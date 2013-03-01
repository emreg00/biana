"""
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

"""


class FastaIterator(object):

	def __init__(self, fd):

		self.fd = fd
		self.current_sequence = []
		self.current_id = None

	def __iter__(self):
		return self

	def next(self):

		for line in self.fd:

			if line.strip()=="":
				continue

			if line.startswith(">"):
				if self.current_id is not None:
					to_return = (self.current_id, "".join(self.current_sequence))
					self.current_id = line[1:].strip()
					self.current_sequence = []
					return to_return
				self.current_id = line[1:].strip()
                                self.current_sequence = []
			else:
				self.current_sequence.append(line.strip())
				
		if self.current_id is not None:
			to_return = (self.current_id, "".join(self.current_sequence))
			self.current_id = None
			self.current_sequence = []
			return to_return

		raise StopIteration


		
