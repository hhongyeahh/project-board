package com.project.projectboard.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;


    public List<Integer> getPaginationBarNumbers(int currentPageNumber,int totalPages) {//리스트 형태 숫자로 내려줌
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2),0);//currentPageNumber 를 중앙에 오도록 + 음수를 방어하기
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);
        return IntStream.range(startNumber,endNumber).boxed().toList();
    }

    public int currentBarLength(){
        return BAR_LENGTH;
    }
}
