from voldemort import StoreClient, VoldemortException
import json
import askService

class Storage():	

	def __init__(self):
		self._connect()
	
	def _connect(self):
		self.s = StoreClient('test',[('localhost',6666)])

	def put(self, key, value):
		rid = long(askService.reqId)
		value = json.dumps(value, separators=(',',':'))
		try:
			self.s.put(str(key),value,None,rid)
		except VoldemortException: 
			self._connect()
			self.s.put(str(key),value,None,rid)
		 
	def get(self,key):
		rid = long(askService.reqId)
		try:
			val = self.s.get(str(key),rid)
		except VoldemortException: 
			self._connect()
			val = self.s.get(str(key),rid)

		if not val:
			return None
		else:
			print val[0][0]
			#open the most recent
			val = val[0][0]
			return json.loads(val)


	def delete(self, key):
		rid = long(askService.reqId)
		try:
			self.s.delete(str(key),None,rid)
		except VoldemortException:  
			self._connect()
			self.s.delete(str(key),None,rid)


