package com.woowa.lotto.domain;

import java.util.List;
import jakarta.persistence.*;

// [수정] @Entity -> @Embeddable로 변경
// Lotto는 이제 다른 엔티티에 포함되는 값 객체
@Embeddable
public class Lotto {
    // [수정] @CollectionTable 어노테이션을 제거하여 JPA가 기본 전략(테이블명 자동 생성)을 사용하도록
    // 이렇게 하면 MyLotto, PurchasedLotto 등 포함하는 엔티티별로 별도의 컬렉션 테이블이 생성
    @ElementCollection
    private final List<Integer> numbers;

    protected Lotto() {
        this.numbers = null;
    }

    public Lotto(List<Integer> numbers) {
        validate(numbers);
        this.numbers = numbers;
    }

    // 이 객체는 로또 한 장에 해당하는 데이터를 이용해 검증, 보너스 숫자 존재, 당첨번호와 몇 개 일치하는 지 스스로 확인
    private void validate(List<Integer> numbers) {
        // 로또 번호 개수 확인
        if (numbers.size() != 6) {
            throw new IllegalArgumentException("[ERROR] 로또 번호는 6개여야 합니다.");
        }

        // 로또 숫자 범위 1~45 확인
        for(Integer num : numbers) {
            if(num < 1 || num > 45) {
                throw new IllegalArgumentException("[ERROR] 로또 번호의 범위는 1~45여야 합니다.");
            }
        }

        // 로또 숫자가 서로 중복되지 않는 지 확인
        if(numbers.size() != numbers.stream().distinct().count()) {
            throw new IllegalArgumentException("[ERROR] 로또 번호는 서로 중복되면 안됩니다.");
        }

    }

    // TODO: 추가 기능 구현
    // 보너스 번호가 리스트 중에 존재하는지 확인해서 true/false 를 돌려주는 메서드
    public boolean hasBonusNum(int bonusNum) {
        return numbers.contains(bonusNum);
    }

    // 당첨 번호와 몇 개가 일치하는 확인해서 일치하는 개수를 돌려주는 메서드
    public int matchCount(Lotto otherLotto) {
        List<Integer> otherNumbers = otherLotto.getNumbers();

        long count = this.numbers.stream()
                .filter(otherNumbers::contains)
                .count();
        return (int) count;
    }

    public List<Integer> getNumbers() {return numbers;}

    // [제거] ID 필드 관련 getter를 제거
}