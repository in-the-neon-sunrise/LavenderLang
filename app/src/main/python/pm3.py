import pymorphy3
morph = pymorphy3.MorphAnalyzer()

rusGender  = ["мужской", "женский", "средний"]
genders = ['masc', 'femn', 'neut']

rusNumber = ["единственное", "множественное"]
numbers = ['sing', 'plur']

rusCase =["именительный", "родительный", "дательный", "винительный",
          "творительный", "предложный"]
cases = ['nomn', 'gent', 'datv', 'accs', 'ablt', 'loct']

rusTime = ["настоящее", "прошедшее", "будущее"]
times = ['pres', 'past', 'futr']

rusPerson = ["первое", "второе", "третье"]
persons = ['1per', '2per', '3per']

rusMood = ["изъявительное", "повелительное"]
moods = ['indc', 'impr']

rusType = ["совершенный", "несовершенный"]
types = ['perf', 'impf']

rusVoice = ["действительный", "страдательный"]
voices = ['actv', 'pssv']

rusDegreeOfComparison = ["положительная", "сравнительная", "превосходная"]

partOfSpeech = {'ADVB', 'ADJF', 'COMP', 'PRTF', 'ADJS', 'CONJ', 'VERB', 'PRTS', 'NPRO',
                'PRED', 'PREP', 'INFN', 'INTJ', 'NUMR', 'GRND', 'NOUN', 'PRCL'}


def inflectAttrs(word, partOfSpeech, attrs):
    w = morph.parse(word)[0]
    res = []
    comp = False
    attrs = list(map(int, attrs[1:-1].split(", ")))
    
    if partOfSpeech == 'NOUN' or partOfSpeech == 'PRONOUN':
        if attrs[0] < len(numbers):
            res.append(numbers[attrs[0]])
        if attrs[1] < len(cases):
            res.append(cases[attrs[1]])
    
    elif partOfSpeech == 'VERB':
        if attrs[0] < len(times):
            res.append(times[attrs[0]])
        if attrs[1] < len(numbers):
            res.append(numbers[attrs[1]])
        if attrs[2] < len(genders):
            res.append(genders[attrs[2]])
        if attrs[3] < len(persons):
            res.append(persons[attrs[3]])
        if attrs[4] < len(moods):
            res.append(moods[attrs[4]])
    
    elif partOfSpeech == 'ADJECTIVE':
        if attrs[0] < len(genders):
            res.append(genders[attrs[0]])
        if attrs[1] < len(numbers):
            res.append(numbers[attrs[1]])
        if attrs[2] < len(cases):
            res.append(cases[attrs[2]])
        if attrs[3] == 1:
            comp = True
        elif attrs[3] == 2:
            res.append('Supr')
    
    elif partOfSpeech == 'PARTICIPLE':
        if attrs[0] < len(times):
            res.append(times[attrs[0]])
        if attrs[1] < len(numbers):
            res.append(numbers[attrs[1]])
        if attrs[2] < len(genders):
            res.append(genders[attrs[2]])
        if attrs[3] < len(cases):
            res.append(cases[attrs[3]])

    inflected_word = w
    for attr in res:
        new_inflected_word = inflected_word.inflect(set([attr]))
        if new_inflected_word != None:
            inflected_word = new_inflected_word
    
    return 'более ' * int(comp) + inflected_word.word


def getAttrs(word):
    w = morph.parse(word)[0]
    inf = w.normal_form
    tag = w.tag

    if 'NOUN' in tag:
        mutableAttrs = [0, 0]
        immutableAttrs = [0]
        if tag.number in numbers:
            mutableAttrs[0] = numbers.index(tag.number)
        if tag.case in cases:
            mutableAttrs[1] = cases.index(tag.case)

        if tag.gender in genders:
            immutableAttrs[0] = genders.index(tag.gender)
        
        return ['NOUN', inf, mutableAttrs, immutableAttrs]

    if 'VERB' in tag or 'INFN' in tag:
        mutableAttrs = [0, 0, 0, 0, 0]
        immutableAttrs = [0, 0]
        if tag.tense in times:
            mutableAttrs[0] = times.index(tag.tense)
        if tag.number in numbers:
            mutableAttrs[1] = numbers.index(tag.number)
        if tag.gender in genders:
            mutableAttrs[2] = genders.index(tag.gender)
        if tag.person in persons:
            mutableAttrs[3] = persons.index(tag.person)
        if tag.mood in moods:
            mutableAttrs[4] = moods.index(tag.mood)

        if tag.aspect in types:
            immutableAttrs[0] = types.index(tag.aspect)
        if tag.voice in voices:
            immutableAttrs[1] = voices.index(tag.voice)

        return ['VERB', inf, mutableAttrs, immutableAttrs]


    if 'ADJF' in tag or 'ADJS' in tag or 'COMP' in tag:
        mutableAttrs = [0, 0, 0, 0]
        immutableAttrs = []
        if tag.gender in genders:
            mutableAttrs[0] = genders.index(tag.gender)
        if tag.number in numbers:
            mutableAttrs[1] = numbers.index(tag.number)
        if tag.case in cases:
            mutableAttrs[2] = cases.index(tag.case)
        if 'Supr' in tag:
            mutableAttrs[3] = 2
        elif 'COMP' in tag:
            mutableAttrs[3] = 1
            
        return ['ADJECTIVE', inf, mutableAttrs, immutableAttrs]
        
    if 'ADVB' in tag or 'PRED' in tag:
        mutableAttrs = []
        immutableAttrs = []
        
        return ['ADVERB', inf, mutableAttrs, immutableAttrs]

    if 'PRTF' in tag or 'PRTS' in tag:
        mutableAttrs = [0, 0, 0, 0]
        immutableAttrs = [0, 0]
        if tag.tense in times:
            mutableAttrs[0] = times.index(tag.tense)
        if tag.number in numbers:
            mutableAttrs[1] = numbers.index(tag.number)
        if tag.gender in genders:
            mutableAttrs[2] = genders.index(tag.gender)
        if tag.case in cases:
            mutableAttrs[3] = cases.index(tag.case)

        if tag.aspect in types:
            immutableAttrs[0] = types.index(tag.aspect)
        if tag.voice in voices:
            immutableAttrs[1] = voices.index(tag.voice)
            
        return ['PARTICIPLE', inf, mutableAttrs, immutableAttrs]
        
    if 'GRND' in tag:
        mutableAttrs = []
        immutableAttrs = [0]
        if tag.aspect in types:
            immutableAttrs[0] = types.index(tag.aspect)
            
        return ['VERBPARTICIPLE', inf, mutableAttrs, immutableAttrs]

    if 'NPRO' in tag:
        mutableAttrs = [0, 0]
        immutableAttrs = [0]
        if tag.number in numbers:
            mutableAttrs[0] = numbers.index(tag.number)
        if tag.case in cases:
            mutableAttrs[1] = cases.index(tag.case)

        if tag.gender in genders:
            immutableAttrs[0] = genders.index(tag.gender)
       
        return ["PRONOUN", inf, mutableAttrs, immutableAttrs]

    if 'NUMR' in tag:
        mutableAttrs = []
        immutableAttrs = []
        
        return ['NUMERAL', inf, mutableAttrs, immutableAttrs]

    else:
        mutableAttrs = []
        immutableAttrs = []
        
        return ['FUNCPART', inf, mutableAttrs, immutableAttrs]


def wrapAttrs(attrs):
    res = '{"partOfSpeech":"' + attrs[0] + '","inf":'
    res += '"' + attrs[1] + '","mutableAttrs":'
    res += ''.join(str(attrs[2]).split()) + ',"immutableAttrs":'
    res += ''.join(str(attrs[3]).split()) + '}'
    return res


def getWrappedAttrs(word):
    return wrapAttrs(getAttrs(word))
