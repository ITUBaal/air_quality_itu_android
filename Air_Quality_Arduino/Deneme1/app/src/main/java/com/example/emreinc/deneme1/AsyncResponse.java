package com.example.emreinc.deneme1;

import java.util.List;

public interface AsyncResponse {
        void dateFinish(List<Integer> colors, int selection, List<Integer> values);
        void dayFinish(List<Integer> values, int selection);
        void weekFinish(List<Integer> values, int selection);
        void monthFinish(List<Integer> values, int selection);
}
