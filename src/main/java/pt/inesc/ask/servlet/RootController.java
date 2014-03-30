package pt.inesc.ask.servlet;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import pt.inesc.ask.domain.Question;


@Controller
public class RootController {

    @Autowired(required = true)
    HttpServletRequest request;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String sayHelloToOpenshift() {
        return "hello";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest req) {
        ModelAndView model = new ModelAndView("index");
        LinkedList<Question> list = new LinkedList<Question>();
        String[] tags = new String[] { "nice", "fixe" };
        list.add(new Question("Dario Post", tags));
        model.addObject("questionList", list);
        return model;
    }
}
