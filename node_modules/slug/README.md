# [slug](https://github.com/Trott/slug)

Slugifies strings, even when they contain Unicode.

Make strings URL-safe.

- Respects [RFC 3986](https://tools.ietf.org/html/rfc3986)
- No dependencies
- Works in browser (`window.slug`) and AMD/CommonJS-flavoured module loaders

```
npm install slug
```

If you are using TypeScript you can install the accompanying types

```
npm install --save-dev @types/slug
```

## Differences between `slug` and `slugify` packages

Here are some key differences between this package and [`slugify`](https://github.com/simov/slugify).

- **Defaults:** `slug` has the `lower` option enabled by default, lowercasing all slugs
  (`'On SALE'` becomes `'on-sale'`).  
  `slugify` has the `lower` option disabled by default (`'On SALE'` becomes `'On-SALE'`).
- **Symbols:** `slug` removes unrecognized symbols (`'$100'` becomes `'100'`, `'<5'` becomes `'5'`, etc.).  
  `slugify` maps them to words (`'$100'` becomes `'dollar100'`, `'<5'` becomes `'less5'`, etc.).
- **Empty Output:** `slug` will return a short, predictable hash (`'   '` becomes `'icag'` and `'🎉'` becomes `'8joiq'`).  
  `slugify` will return an empty string (`'   '` and `'🎉'` become `''`).
- **Stability:** `slug` is planning [a new release](https://github.com/Trott/slug/blob/beta/CHANGELOG.md) that will drop support for CommonJS
  and only support ESM modules.  
  `slugify` will continue to support CommonJS and is likely to remain stable for the foreseeable future.

## Example

```javascript
var slug = require('slug')
var print = console.log.bind(console, '>')

print(slug('i love unicode'))
// > i-love-unicode

print(slug('i love unicode', '_')) // If you prefer something else than `-` as separator
// > i_love_unicode

slug.charmap['♥'] = 'freaking love' // change default charmap or use option {charmap:{…}} as 2. argument
print(slug('I ♥ UNICODE'))
// > i-freaking-love-unicode

// To reset modifications to slug.charmap, use slug.reset():
slug.reset()
print(slug('I ♥ UNICODE'))
// > i_unicode

print(slug('Telephone-Number')) // lower case by default
// > telephone-number

print(slug('Telephone-Number', {lower: false})) // If you want to preserve case
// > Telephone-Number

// We try to provide sensible defaults.
// So Cyrillic text will be transliterated as if it were Russian:
print(slug('маленький подъезд'))
// > malenkij-poduezd

// But maybe you know it's Bulgarian:
print(slug('маленький подъезд', { locale: 'bg' }))
// > malenykiy-podaezd

// To set the default locale:
slug.setLocale('bg')
print(slug('маленький подъезд'))
// > malenykiy-podaezd

print(slug('unicode is ☢'))
// > unicode-is

slug.extend({'☢': 'radioactive'})
print(slug('unicode ♥ is ☢'))
// > unicode-is-radioactive

// slug.extend() modifies the default charmap for the entire process.
// If you need to reset charmap, multicharmap, and the default locale, use slug.reset():

slug.reset()
print(slug('unicode ♥ is ☢'))
// > unicode-is

// Custom removal of characters from resulting slug. Let's say that we want to
// remove all numbers for some reason.
print(slug('one 1 two 2 three 3'))
// > one-1-two-2-three-3
print(slug('one 1 two 2 three 3', { remove: /[0-9]/g }))
// > one-two-three
```

## options

```javascript
// options is either object or replacement (sets options.replacement)
slug('string', [{options} || 'replacement']);
```

```javascript
slug.defaults.mode ='pretty';
slug.defaults.modes['rfc3986'] = {
    replacement: '-',      // replace spaces with replacement
    remove: null,          // (optional) regex to remove characters
    lower: true,           // result in lower case
    charmap: slug.charmap, // replace special characters
    multicharmap: slug.multicharmap, // replace multiple code unit characters
    trim: true,             // trim leading and trailing replacement chars
    fallback: true          // use base64 to generate slug for empty results
};
slug.defaults.modes['pretty'] = {
    replacement: '-',
    remove: null,
    lower: false,
    charmap: slug.charmap,
    multicharmap: slug.multicharmap,
    trim: true,
    fallback: true
};
```
