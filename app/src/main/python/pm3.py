import pymorphy3
morph = pymorphy3.MorphAnalyzer()

genders = ['masc', 'femn', 'neut']
numbers = ['sing', 'plur']
cases = ['nomn', 'gent', 'datv', 'accs', 'ablt', 'loct']
times = ['pres', 'past', 'futr']
persons = ['1per', '2per', '3per']
moods = ['indc', 'impr']
types = ['perf', 'impf']
voices = ['actv', 'pssv']


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
        time, number, gender, person, mood, isinf = attrs
        
        if isinf == 0:
            return w.normal_form
        
        if time >= len(times) or time == 1: # past tense
            if gender < len(genders):
                res.append(genders[gender])
                
        else: # present or future tense
            if person < len(persons):
                res.append(persons[person])
                
        if number < len(numbers):
            res.append(numbers[number])
        if time < len(times):
            res.append(times[attrs[0]])
        if mood < len(moods):
            res.append(moods[mood])
    
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

    elif partOfSpeech == 'ADVERB':
        if attrs[0] == 1:
            comp = True
        elif attrs[0] == 2:
            res.append('Supr')

    inflected_word = w
    for attr in res:
        new_inflected_word = inflected_word.inflect(set([attr]))
        if new_inflected_word != None:
            inflected_word = new_inflected_word
    
    return 'более ' * int(comp) + inflected_word.word

def getNormalForm(word):
    if ' ' in word:
            return ' '.join([getNormalForm(w) for w in word.split()])
    return morph.parse(word)[0].normal_form
