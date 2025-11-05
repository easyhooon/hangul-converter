package com.github.easyhooon.qwerty2hangul

import org.junit.Test

import org.junit.Assert.*

class Qwerty2HangulTest {

    @Test
    fun `테스트 기본 단어 변환`() {
        // 기본 단어 테스트
        assertEquals("안녕", QwertyHangul.engToKor("dkssud"))
        assertEquals("안녕하세요", QwertyHangul.engToKor("dkssudgktpdy"))
        assertEquals("감사합니다", QwertyHangul.engToKor("rkatkgkqslek"))
    }

    @Test
    fun `테스트 원본 예시`() {
        assertEquals("안녕", QwertyHangul.engToKor("dkssud"))
        assertEquals("반갑습니다", QwertyHangul.engToKor("qksrkqtmqslek"))
        assertEquals("감사합니다", QwertyHangul.engToKor("rkatkgkqslek"))
    }

    @Test
    fun `테스트 초성만 있는 경우`() {
        // 초성만 있는 경우
        assertEquals("ㄱ", QwertyHangul.engToKor("r"))
        assertEquals("ㄴㄷㄹ", QwertyHangul.engToKor("sef"))
    }

    @Test
    fun `테스트 복합 모음`() {
        // 복합 모음 테스트
        assertEquals("과", QwertyHangul.engToKor("rhk"))
        assertEquals("귀", QwertyHangul.engToKor("rnl"))
        assertEquals("의", QwertyHangul.engToKor("dml"))
    }

    @Test
    fun `테스트 복합 받침`() {
        // 복합 받침 테스트
        assertEquals("값", QwertyHangul.engToKor("rkqt"))
        assertEquals("않", QwertyHangul.engToKor("dksg"))
        assertEquals("닭", QwertyHangul.engToKor("ekfr"))
    }

    @Test
    fun `테스트 문장`() {
        // 문장 테스트
        assertEquals(
            "안녕하세요 반갑습니다",
            QwertyHangul.engToKor("dkssudgktpdy qksrkqtmqslek")
        )
        assertEquals(
            "한글 입력이 어려워요",
            QwertyHangul.engToKor("gksrmf dlqfurdl djfudnjdy")
        )
    }

    @Test
    fun `테스트 비한글 문자 포함`() {
        // 영어와 숫자, 특수문자 혼합
        assertEquals(
            "안녕123",
            QwertyHangul.engToKor("dkssud123")
        )
        assertEquals(
            "안녕123!@",
            QwertyHangul.engToKor("dkssud123!@")
        )
    }

    @Test
    fun `테스트 빈 입력`() {
        // 빈 입력 테스트
        assertEquals("", QwertyHangul.engToKor(""))
    }

    @Test
    fun `테스트 대문자 쌍자음`() {
        // 쌍자음 테스트
        assertEquals("까", QwertyHangul.engToKor("Rk"))
        assertEquals("짜", QwertyHangul.engToKor("Wk"))
        assertEquals("빠", QwertyHangul.engToKor("Qk"))
    }
}