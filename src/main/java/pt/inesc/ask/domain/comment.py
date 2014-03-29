import hashlib
from storage.service import StorageService

storage = StorageService()

class Comment:
	def __init__(self,text,author,answerID,comment_id = None):
		if comment_id is None:
			m = hashlib.md5()
			m.update(text+author+answerID)
			self._id = "com_"+m.hexdigest()
		else:
			self._id = comment_id
		self.text = text
		self.author = author


	@classmethod
	def build(self,text,author,answerID):
		return Comment(text, author, answerID).save()

	@classmethod
	def getFromId(self,commentId):
		comment = storage.get(commentId)
		if comment is not None:
			comment = Comment.fromDictionary(comment)
		return comment

	@classmethod
	def fromDictionary(self,array):
		return Comment(array['text'], array['author'], None, array['_id'])

	######################################################################################

	def delete(self):
		storage.delete(self._id)

	def update(self,text):
		self.text = text
		self.save()

	def save(self):
		objectDict = self.__dict__
		storage.put(objectDict['_id'],objectDict)		
		return self
