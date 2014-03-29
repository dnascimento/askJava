from models.question import Question
import models.askExceptions
import hashlib

reqId = 0

#Singleton
class AskService:
	_instance = None
	def __new__(cls,*args,**kwargs):
		if not cls._instance:
			cls._instance = super(Singleton,cls).__new__(cls, *args, **kwargs)
		return cls._instance


	def getQuestion(self,questionTitle):
		return Question.getFromId("ques_"+questionTitle)

	############### Question #####################
	def addNewQuestion(self,title,text,tags,author,rid):
		global reqId
		reqId = rid
		return Question.build(title,text,tags,author)

	def getQuestionData(self,questionTitle,rid):
		global reqId
		reqId = rid
		return self.getQuestion(questionTitle)

	def deleteQuestion(self,questionTitle,rid):
		global reqId
		reqId = rid
		self.getQuestion(questionTitle).delete()

	def getQuestionList(self,maxQuestions,rid):
		global reqId
		reqId = rid
		return Question.getList(maxQuestions)
	
	############### Answer #####################
	def addAnswer(self,questionTitle,text,author,rid):
		global reqId
		reqId = rid
		self.getQuestion(questionTitle).addAnswer(author, text)

	def updateAnswer(self,questionTitle,answerID,text,rid):
		global reqId
		reqId = rid
		self.getQuestion(questionTitle).updateAnswer(answerID,text)

	def delAnswer(self,questionTitle,answerID,rid):
		global reqId
		reqId = rid
		self.getQuestion(questionTitle).deleteAnswer(answerID)

	############# Comment ##################
	def addComment(self,questionTitle,answerID,text,author,rid):
		global reqId
		reqId = rid
		self.getQuestion(questionTitle).addComment(answerID,text,author)

	def deleteComment(self,questionTitle,answerID,commentID,rid):
		global reqId
		reqId = rid
		self.getQuestion(questionTitle).deleteComment(answerID,commentID)

	def updateComment(self,questionTitle,answerID,commentID,text,rid):
		global reqId
		reqId = rid
		self.getQuestion(questionTitle).updateComment(answerID,commentID,text)

	############### Vote #####################
	def voteUp(self,questionTitle,answerID,rid):
		global reqId
		reqId = rid
		self.getQuestion(questionTitle).voteUp(answerID)

	def voteDown(self,questionTitle,answerID,rid):
		global reqId
		reqId = rid
		self.getQuestion(questionTitle).voteDown(answerID)



	

































