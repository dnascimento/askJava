from comment import Comment
import askExceptions
import hashlib
from storage.service import StorageService

storage = StorageService()

		
class Answer:
	def __init__(self,author,text,questionTitle, isQuestion,votes = 0,commentsIds = [],answer_id = None):
		if answer_id is None:
			m = hashlib.md5()
			m.update(text+author+questionTitle)
			self._id = "ans_"+m.hexdigest()
		else:
			self._id = answer_id

		self.author = author
		self.text = text
		self.isQuestion = isQuestion
		self.votes = votes
		self.commentsIds = commentsIds

	@classmethod
	def build(self,author,text,title,isQuestion):
		return Answer(author, text, title, isQuestion).save()

	@classmethod
	def getFromId(self,answerId):
		answer = storage.get(answerId)
		if answer is not None:
			answer = Answer.fromDictionary(answer)
			answer.LoadComments()
		return answer

	@classmethod
	def fromDictionary(self,array):
		return Answer(array['author'], array['text'], None, array['isQuestion'],array['votes'],array['commentsIds'],array['_id'])

	def LoadComments(self):
		comments = []
		for commentID in self.commentsIds:
			comm = Comment.getFromId(commentID)
			if comm is not None:
				comments.append(comm)
		self.comments = comments
	########################################
	def addComment(self,text,author):
		comment = Comment.build(text,author,self._id)
		self.commentsIds.append(comment._id)
		self.save()

	def deleteComment(self,commentID):
		comment = self.getComment(commentID).delete()
		self.commentsIds.remove(commentID)
		self.save()


	def updateComment(self,commentID,text):
		self.getComment(commentID).update(text)

	def update(self,text):
		self.text = text
		self.save()

	def voteUp(self):
		self.votes += 1
		self.save()

	def voteDown(self):
		self.votes -= 1
		self.save()

	def delete(self):
		for comment in self.comments:
			comment.delete()
		storage.delete(self._id)

	def getComment(self,commentID):
		for comment in self.comments:
			if comment._id == commentID:
				return comment
		raise askExceptions.NotExist('comment',"Unknown Comment:"+str(commentID))


	################ CASTS ###################
	def save(self):
		objectDict = self.__dict__
		objectDict['comments'] = None
		storage.put(objectDict['_id'],objectDict)
		return self


