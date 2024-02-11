export type ValidationRule<T> = (val: T) => true | string
export type ValidationRules<T = string> = ValidationRule<T>[]
export type ValidationRuleMap = Record<string, ValidationRules>

export const requiredString: ValidationRules = [
  (val: string) => val.length > 0 || 'Required',
]

