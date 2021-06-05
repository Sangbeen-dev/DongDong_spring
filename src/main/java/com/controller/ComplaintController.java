package com.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dto.CommentsDTO;
import com.dto.ComplaintDTO;
import com.dto.MemberDTO;
import com.dto.PostDTO;
import com.service.CommentsService;
import com.service.ComplaintService;
import com.service.MemberService;
import com.service.PostService;

@Controller
public class ComplaintController {
	@Autowired
	ComplaintService coService;
	@Autowired
	MemberService mService;
	@Autowired
	PostService pService;
	@Autowired
	CommentsService cService;

	@RequestMapping(value = "/loginCheck/complaintAccept")
	public @ResponseBody String ComplaintAccept(HttpSession session, @RequestParam Map<String, String> map) {
		MemberDTO dto = (MemberDTO)session.getAttribute("login");
		String coTarget = map.get("coTarget");
		String userid = map.get("userid");
		String coContent = map.get("coContent");
		int coType = Integer.parseInt(map.get("coType"));
		String returnValue = "";

		ComplaintDTO coDTO = new ComplaintDTO();
		
		if(coType==1) {
			coDTO.setTargetUserid(coTarget);
			coDTO.setTargetContent("");
			MemberDTO mDTO = mService.mypage(coTarget);
			coDTO.setTargetTitle(mDTO.getNickName());
			coDTO.setTargetImage(mDTO.getUserimage());
		}else if(coType==2) {
			PostDTO pDTO = pService.getPostByPNum(Integer.parseInt(coTarget));
			coDTO.setTargetTitle(pDTO.getpTitle());
			coDTO.setTargetContent(pDTO.getpContent());
			coDTO.setTargetImage(pDTO.getpImage());
			coDTO.setTargetUserid(pDTO.getUserid());
		} else if(coType==3) {
			CommentsDTO cDTO = cService.getCommentByCNum(Integer.parseInt(coTarget));
			coDTO.setTargetTitle(cDTO.getNickName());
			coDTO.setTargetContent(cDTO.getcContent());
			coDTO.setTargetUserid(cDTO.getUserid());
			coDTO.setTargetImage(cDTO.getUserimage());
		}
		coDTO.setCoTarget(coTarget);
		coDTO.setUserid(userid);
			
		if(coService.checkDuplication(coDTO)) {
			returnValue = "dup"; 
		} else {
			coDTO.setCoContent(coContent);
			coDTO.setCoType(coType);
				
			int insertResult = coService.insertComplaint(coDTO);
				
			if(insertResult!=1) { // 게시글 업데이트가 실패했을 경우 
				returnValue = "false"; 
		    } else {	
		    	returnValue = "true"; 
		    }	
		}	
		return returnValue;
	}
	
	@RequestMapping(value = "/loginCheck/complaintDetail")
	public ModelAndView complaintDetail(HttpSession session, @RequestParam Map<String, String> map) {
		String cNum = map.get("cNum");
		String coType = map.get("coType");
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("cNum",cNum);
		mav.addObject("coType",coType);
		mav.setViewName("complaint/complaintDetail");

		return mav;
	}
}
