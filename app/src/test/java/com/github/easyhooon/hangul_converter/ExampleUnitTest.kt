package com.github.easyhooon.hangul_converter

import org.junit.Test

import org.junit.Assert.*

class HangulConverterTest {

    @Test
    fun `테스트 기본 단어 변환`() {
        // 기본 단어 테스트
        assertEquals("안녕", HangulConverter.engToKor("dkssud"))
        assertEquals("안녕하세요", HangulConverter.engToKor("dkssudgktpdy"))
        assertEquals("감사합니다", HangulConverter.engToKor("rkatkgkqslek"))
    }

    @Test
    fun `테스트 원본 예시`() {
        assertEquals("안녕", HangulConverter.engToKor("dkssud"))
        assertEquals("반갑습니다", HangulConverter.engToKor("qksrkqtmqslek"))
        assertEquals("감사합니다", HangulConverter.engToKor("rkatkgkqslek"))
    }

    @Test
    fun `테스트 초성만 있는 경우`() {
        // 초성만 있는 경우
        assertEquals("ㄱ", HangulConverter.engToKor("r"))
        assertEquals("ㄴㄷㄹ", HangulConverter.engToKor("sef"))
    }

    @Test
    fun `테스트 복합 모음`() {
        // 복합 모음 테스트
        assertEquals("과", HangulConverter.engToKor("rhk"))
        assertEquals("귀", HangulConverter.engToKor("rnl"))
        assertEquals("의", HangulConverter.engToKor("dml"))
    }

    @Test
    fun `테스트 복합 받침`() {
        // 복합 받침 테스트
        assertEquals("값", HangulConverter.engToKor("rkqt"))
        assertEquals("않", HangulConverter.engToKor("dksg"))
        assertEquals("닭", HangulConverter.engToKor("ekfr"))
    }

    @Test
    fun `테스트 문장`() {
        // 문장 테스트
        assertEquals(
            "안녕하세요 반갑습니다",
            HangulConverter.engToKor("dkssudgktpdy qksrkqtmqslek")
        )
        assertEquals(
            "한글 입력이 어려워요",
            HangulConverter.engToKor("gksrmf dlqfurdl djfudnjdy")
        )
    }

    @Test
    fun `테스트 비한글 문자 포함`() {
        // 영어와 숫자, 특수문자 혼합
        assertEquals(
            "안녕123",
            HangulConverter.engToKor("dkssud123")
        )
        assertEquals(
            "안녕123!@",
            HangulConverter.engToKor("dkssud123!@")
        )
    }

    @Test
    fun `테스트 빈 입력`() {
        // 빈 입력 테스트
        assertEquals("", HangulConverter.engToKor(""))
    }

    @Test
    fun `테스트 대문자 쌍자음`() {
        // 쌍자음 테스트
        assertEquals("까", HangulConverter.engToKor("Rk"))
        assertEquals("짜", HangulConverter.engToKor("Wk"))
        assertEquals("빠", HangulConverter.engToKor("Qk"))
    }
}