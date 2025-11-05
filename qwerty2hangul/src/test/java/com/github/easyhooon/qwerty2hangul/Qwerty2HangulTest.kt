package com.github.easyhooon.qwerty2hangul

import org.junit.Test

import org.junit.Assert.*

class Qwerty2HangulTest {

    @Test
    fun `테스트 기본 단어 변환`() {
        // 기본 단어 테스트
        assertEquals("안녕", Qwerty2Hangul.engToKor("dkssud"))
        assertEquals("안녕하세요", Qwerty2Hangul.engToKor("dkssudgktpdy"))
        assertEquals("감사합니다", Qwerty2Hangul.engToKor("rkatkgkqslek"))
    }

    @Test
    fun `테스트 원본 예시`() {
        assertEquals("안녕", Qwerty2Hangul.engToKor("dkssud"))
        assertEquals("반갑습니다", Qwerty2Hangul.engToKor("qksrkqtmqslek"))
        assertEquals("감사합니다", Qwerty2Hangul.engToKor("rkatkgkqslek"))
    }

    @Test
    fun `테스트 초성만 있는 경우`() {
        // 초성만 있는 경우
        assertEquals("ㄱ", Qwerty2Hangul.engToKor("r"))
        assertEquals("ㄴㄷㄹ", Qwerty2Hangul.engToKor("sef"))
    }

    @Test
    fun `테스트 복합 모음`() {
        // 복합 모음 테스트
        assertEquals("과", Qwerty2Hangul.engToKor("rhk"))
        assertEquals("귀", Qwerty2Hangul.engToKor("rnl"))
        assertEquals("의", Qwerty2Hangul.engToKor("dml"))
    }

    @Test
    fun `테스트 복합 받침`() {
        // 복합 받침 테스트
        assertEquals("값", Qwerty2Hangul.engToKor("rkqt"))
        assertEquals("않", Qwerty2Hangul.engToKor("dksg"))
        assertEquals("닭", Qwerty2Hangul.engToKor("ekfr"))
    }

    @Test
    fun `테스트 문장`() {
        // 문장 테스트
        assertEquals(
            "안녕하세요 반갑습니다",
            Qwerty2Hangul.engToKor("dkssudgktpdy qksrkqtmqslek")
        )
        assertEquals(
            "한글 입력이 어려워요",
            Qwerty2Hangul.engToKor("gksrmf dlqfurdl djfudnjdy")
        )
    }

    @Test
    fun `테스트 비한글 문자 포함`() {
        // 영어와 숫자, 특수문자 혼합
        assertEquals(
            "안녕123",
            Qwerty2Hangul.engToKor("dkssud123")
        )
        assertEquals(
            "안녕123!@",
            Qwerty2Hangul.engToKor("dkssud123!@")
        )
    }

    @Test
    fun `테스트 빈 입력`() {
        // 빈 입력 테스트
        assertEquals("", Qwerty2Hangul.engToKor(""))
    }

    @Test
    fun `테스트 대문자 쌍자음`() {
        // 쌍자음 테스트
        assertEquals("까", Qwerty2Hangul.engToKor("Rk"))
        assertEquals("짜", Qwerty2Hangul.engToKor("Wk"))
        assertEquals("빠", Qwerty2Hangul.engToKor("Qk"))
    }
}