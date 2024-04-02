import pymorphy3

def test() -> str:
    morph = pymorphy3.MorphAnalyzer()
    return str(morph.parse("солнышка"))
